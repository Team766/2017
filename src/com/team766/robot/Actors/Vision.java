package com.team766.robot.Actors;

import interfaces.CameraInterface;

import java.util.ArrayList;

import lib.Actor;
import lib.ConstantsFileReader;
import lib.LogFactory;
import lib.Message;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.team766.lib.Messages.DriveDistance;
import com.team766.lib.Messages.DriveIntoPeg;
import com.team766.lib.Messages.MotorCommand;
import com.team766.lib.Messages.MotorCommand.Motor;
import com.team766.lib.Messages.StartTrackingPeg;
import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.VisionStatusUpdate;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;

public class Vision extends Actor{
	
	private final long RUN_TIME = 10;
	
	private int hueMin = 38;
	private int satMin = 92;
	private int valueMin = 56;
	private int hueMax = 96;
	private int satMax = 255;
	private int valueMax = 165;
	
	private final int SUM_THRESHOLD = 8;
	private final int LOW_THRESHOLD = 3;
	private final double MAGNITUDE_THRESH = 0.5;
	private final double SimularityThresh = 5;
	private final double AreaSimularityThresh = 0.17;
	private final double WIDTH_THRESH = 0.7;
	private final double X_OFFSET_THRESH = 10;
	
	private final double RECT_WIDTH = 10.25;
	private final double FOCAL_LENGTH = 160;//2.8; //pixels
	private final double CENTER_X = 80;
	private final double CENTER_Y = 60;

	private double outputDist;
	private double outputAngle;
	
	private boolean trackingEnabled = false;
	private boolean drivingEnabled = false;
	private final int UPDATE_RATE = 50;
	private int counter;
	private final double MAX_COUNT = 50;
	
	CameraInterface camServer = HardwareProvider.getInstance().getCameraServer();
	
	Message currentMessage;
	
	@Override
	public void init() {
		acceptableMessages = new Class[]{StartTrackingPeg.class, Stop.class, DriveIntoPeg.class};
		
		LogFactory.getInstance("General").print("Vision: INIT");
		outputDist = 0;
		outputAngle = 0;
		counter = UPDATE_RATE;
		
		done = false;
	}
	
	@Override
	public void run() {
		Mat img = new Mat();
		while(true){
			itsPerSec++;
			sleep(RUN_TIME);
			
			if(newMessage()){
				currentMessage = readMessage();
				if(currentMessage == null)
					break;
				
				if(currentMessage instanceof StartTrackingPeg){
					trackingEnabled = true;
					done = false;
					counter = UPDATE_RATE;
					//Needs to send message when tracking
//					sendMessage(new DriveSideways(-getDist() * Math.sin(Math.toRadians(getAngle()))));
				}
				else if(currentMessage instanceof DriveIntoPeg){
					drivingEnabled = true;
					done = false;
					counter = UPDATE_RATE;
				}
				else if(currentMessage instanceof Stop){
					//Stop
					trackingEnabled = false;
					done = true;
					counter = UPDATE_RATE;
					sendMessage(new MotorCommand(0, Motor.centerDrive));
				}
					
			}
			
			camServer.getFrame(img);
			if(img == null || img.empty()){
				LogFactory.getInstance("Vision").print("Vision: No Input Image");
				continue;
			}
			//Begin processing image below
			Mat out = process(img);
			if(out == null){
				LogFactory.getInstance("Vision").print("Vision: No/Not enough Countours found");
				continue;
			}
			
			camServer.putFrame(out);
			
			if(counter >= UPDATE_RATE){
				if(trackingEnabled){
					sendMessage(new MotorCommand(-getDist() * Math.sin(Math.toRadians(getAngle()) * ConstantsFileReader.getInstance().get("centerDriveP")), Motor.centerDrive));
	//				sendMessage(new DriveSideways(-getDist() * Math.sin(Math.toRadians(getAngle()))));
					done = Math.abs(getDist() * Math.sin(Math.toRadians(getAngle()))) < Constants.ALLIGNING_SIDEWAYS_DIST_THRESH;
				}
				
				if(drivingEnabled){
					sendMessage(new DriveDistance(getDist(), 0.0));
					done = getDist() < Constants.DRIVE_INTO_PEG_THRESH;
				}
				
				counter = 0;
			}
			counter++;
			
			sendMessage(new VisionStatusUpdate(done, currentMessage, getAngle(), getDist()));
		}
	}
	
	public void step(){
	}
	
	public double getAngle(){
		return outputAngle;
	}
	
	public double getDist(){
		return outputDist;
	}
	
	private Mat process(Mat in){
		Mat output = in.clone();
		
		Imgproc.cvtColor(output, output, Imgproc.COLOR_BGR2HSV);
		
		Core.inRange(output, new Scalar(hueMin, satMin, valueMin), new Scalar(hueMax, satMax, valueMax), output);
		
		Imgproc.blur(output, output, new Size(3,3));
		
		Imgproc.Canny(output, output, 100d, 300d);
		
		Mat display = output.clone();
		Imgproc.cvtColor(display, display, Imgproc.COLOR_GRAY2BGR);
		
		//Display Hough Lines
		Mat cardLines = new Mat();
		Imgproc.HoughLinesP(output, cardLines, 1, Math.PI/90, 10, 5, 20);
		
		double[] angles = new double[cardLines.cols()];
		
		for(int i = 0; i < cardLines.cols(); i++){
			angles[i] = Math.atan2(cardLines.get(0, i)[3] - cardLines.get(0, i)[1], cardLines.get(0, i)[2] - cardLines.get(0, i)[0]);
		}
				
		//Calculate gradient field
		int[] histogram = new int[90];
				
		for(int i = 0; i < angles.length; i++){
			int ang = (int)(angles[i]*180/Math.PI);
			int bucket = (ang + 360) % 90;
				histogram[bucket]++; 
		}
		
		int max = histogram[0];
		int angle = 0;
		for(int i = 0; i < histogram.length; i++){
			//System.out.println(i + " " + histogram[i]);
			if(histogram[i] > max){
				max = histogram[i];
				angle = i;
			}
		}
		//Account for neg numbers in the array
		if (angle > 45)
			angle -= 90;
		
//		System.out.println("Angle: " + angle);
						
		//Rotate image x degrees
		Point src_center = new Point(output.cols()/2.0F, output.rows()/2.0F);
		Mat rot_mat = Imgproc.getRotationMatrix2D(src_center, angle, 1.0);
		Imgproc.warpAffine(output, output, rot_mat, output.size());
		
		//Rotate display image
		Imgproc.warpAffine(display, display, rot_mat, output.size());
		

		double[] sums = new double[output.width()];
		
		for(int i = 0; i < output.width(); i++){
			for(int j = 0; j < output.height(); j++){
				sums[i] += output.get(j, i)[0]/255.0;
			}
		}
//		System.out.println(Arrays.toString(sums));
		
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(output, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//		Imgproc.drawContours(display, contours, -1, new Scalar(0, 255, 0));
		
		if(contours.isEmpty())
			return null;
		
		MatOfPoint2f[] contours_poly = new MatOfPoint2f[contours.size()];
		for(int i = 0; i < contours_poly.length; i++){
			contours_poly[i] = new MatOfPoint2f();
		}
		Rect[] boundRect = new Rect[contours.size()];
		for(int i = 0; i < boundRect.length; i++){
			boundRect[i] = new Rect();
		}
		
		if(boundRect.length == 0)
			return null;
		
		//Important boundingRects
		Rect[] imporRects = new Rect[3];
		int maxIndex = 0;
		imporRects[maxIndex] = new Rect(0,0,0,0);
		
		for(int i = 0; i < contours.size(); i++ ){
			Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contours_poly[i], 3.0, true);
			boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contours_poly[i].toArray()));
	    }
		
		//Remove duplicates
		for(int i = 0; i < boundRect.length; i++){
			for(int j = 0; j < boundRect.length; j++){
				if(i == j || boundRect[i] == null || boundRect[j] == null)
					continue;
				if((Math.abs(boundRect[i].height - boundRect[j].height) < SimularityThresh &&
						Math.abs(boundRect[i].x - boundRect[j].x) < SimularityThresh) ||
						boundRect[j].width > boundRect[j].height)
					boundRect[j] = null;
			}
		}
//		System.out.println("Bounded: " + Arrays.toString(boundRect));
		//Check if it should have 3 rects or just 2
		
		boolean pairFound = false;
		//Check to see if there are two rects with same width, and if multiple largest area prioritized
		if(arraySize(boundRect) > 2){
//			System.out.println("Checking: " + Arrays.toString(boundRect));
			imporRects[0] = new Rect(0,0,0,0);
			imporRects[1] = new Rect(0,0,0,0);
			for(Rect r : boundRect){
				for(Rect r1 : boundRect){
					if(r == r1 || r == null || r1 == null)
						continue;
					//Same Area: two squares among alot of others
					if((Math.abs(1.0 - (Math.max(r.height, r1.height) / Math.min(r.height, r1.height))) < WIDTH_THRESH &&  //Areas have same height
							(Math.abs(1.0 - Math.max(r.width, r1.width) / Math.min(r.width, r1.width)) < WIDTH_THRESH) &&
							(r.height/r.width == 2) && 
							(r1.height/r1.width == 2) && 
							((Math.abs(1.0 - Math.max(r.area(), r1.area()) / Math.min(r.area(), r1.area()))) < AreaSimularityThresh) && //Areas similar
							(r.area() > imporRects[0].area() && r1.area() > imporRects[1].area()))){	//Area bigger than previously found rects
//						System.out.println("Same AREA: " + r.area() + "\t" + r1.area());
						imporRects[0] = r;
						imporRects[1] = r1;
						pairFound = true;
					}
				}
			}
		}
		if(!pairFound && arraySize(boundRect) > 2){
			for(Rect r : boundRect){
				for(Rect r1 : boundRect){
					for(Rect r2 : boundRect){
						if(r == r1 || r == r2 || r1 == r2 || r2 == null || r == null || r1 == null)
							continue;
						
						//Check same width and diff x-values
						//r = big one, r1 = bottom small, r2 = top small
						if(((Math.abs(1.0 - Math.max(r.width, r1.width) / Math.min(r.width, r1.width)) < WIDTH_THRESH && 
								Math.abs(1.0 - Math.max(r.width, r2.width) / Math.min(r.width, r2.width)) < WIDTH_THRESH)) && 
								(Math.abs(1.0 - Math.max(r1.x, r.x) / Math.min(r1.x, r.x)) < WIDTH_THRESH) && 
								(r.height/r.width == 2) && 
								((r1.br().y - r2.tl().y)/((r1.width + r2.width)/2) == 2) && 
								Math.abs(1.0 - Math.max(r.height, (r1.br().y - r2.tl().y)) / Math.min(r.height, (r1.br().y - r2.tl().y))) < WIDTH_THRESH){
								
//							System.out.println("Same Stuff");
							//Only save the first ones or bigger areas
							if((imporRects[0] == null || imporRects[1] == null || imporRects[2] == null) ||
									 (r.area()> imporRects[0].area() && r1.area()> imporRects[1].area() && r2.area()> imporRects[2].area())){
								imporRects[0] = r;
								imporRects[1] = r1;
								imporRects[2] = r2;
							}
						}
					}
				}
			}
		}
				
		//Remove 3 max rectangles and add to impor Rects
		if(!pairFound){
			for(int i = 0; i < imporRects.length; i++){
	//			System.out.println("Bound: " + Arrays.toString(boundRect));
				int indexWithMax = findMaxRectIndex(boundRect);
				if(indexWithMax < 0){
					continue;
				}
				imporRects[i] = boundRect[indexWithMax];
				boundRect[indexWithMax] = null;
			}
		}
		
		//Check if we need the third rect
		if(imporRects[1] != null && imporRects[2] != null &&
				Math.abs(imporRects[1].x - imporRects[2].x) > X_OFFSET_THRESH)
				imporRects[2] = null;		
		
//		System.out.println("Important: " + Arrays.toString(imporRects));
		
		if(imporRects[0] != null && imporRects[1] != null){
			Point centerPoint = new Point((imporRects[0].x + imporRects[0].width/2.0  + imporRects[1].x + imporRects[1].width/2.0)/2.0, CENTER_Y);
			outputAngle = Math.toDegrees(convertPointToAngle(centerPoint));
		}
		double maxX = 0;
		double minX = output.width();
		if(arraySize(imporRects) > 2){
			maxX = Math.max(Math.max(imporRects[0].br().x, imporRects[1].br().x), imporRects[2].br().x);
			minX = Math.min(Math.min(imporRects[0].tl().x, imporRects[1].tl().x), imporRects[2].tl().x);
		}else if(arraySize(imporRects) == 2){
			maxX = Math.max(imporRects[0].br().x, imporRects[1].br().x);
			minX = Math.min(imporRects[0].tl().x, imporRects[1].tl().x);
		}
		
		for(Rect r : imporRects){
			if(r == null)
				continue;
			Imgproc.rectangle(display, r.tl(), r.br(), new Scalar(0, 255, 0));
		}
		
		//Set values
		outputDist = ((RECT_WIDTH * FOCAL_LENGTH) / (maxX - minX))/12.0;	//Inch to feet
		Imgproc.putText(display, "dist: " + outputDist, new Point(10, 40), 1, 0.5, new Scalar(0, 255, 255));
//		Imgproc.putText(display, "horDist: " + (-getDist() * Math.sin(Math.toRadians(getAngle()))), new Point(10, 40), 1, 0.5, new Scalar(0, 255, 255));
		
		return display;
	}
	
	private int arraySize(Object[] in){
		int count = 0;
		for(Object i : in){
			if(i != null)
				count++;
		}
		return count;
	}
	
	private int findMaxRectIndex(Rect[] rectangles){
		int maxIndex = 0;
		while(maxIndex < rectangles.length - 1 && rectangles[maxIndex] == null){
			maxIndex++;
		}
		if(rectangles[maxIndex] == null)
			return -1;
		double maxArea = rectangles[maxIndex].area();
		for(int i = maxIndex; i < rectangles.length; i++){
			if(rectangles[i] == null)
				continue;
			if(rectangles[i].area() > maxArea){
				maxArea = rectangles[i].area();
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	@Override
	public String toString(){
		return "Actor:\tVision";
	}
	
	private boolean contains(double[] array, double value){
		for(double x : array){
			if(value == x)
				return true;
		}
		return false;
	}
	
	//Convert pixel point on screen, to heading error
	public double convertPointToAngle(Point point){
			double[] pixel = {point.x - CENTER_X, point.y - CENTER_Y, FOCAL_LENGTH};
			double[] center = {0, 0, FOCAL_LENGTH};
			
			double dot = dotProduct(pixel, center);
			
			//Find angle between vectors using dot product
			return ((point.x > CENTER_X)? 1 : -1) * Math.acos(dot/(magnitudeVector(pixel) * magnitudeVector(center)));
	}
	
	private double magnitudeVector(double[] vector){
		double total = 0;
		
		for(double d : vector){
			total += Math.pow(d, 2);
		}
		
		return Math.sqrt(total);
	}
	
	private double dotProduct(double[] a, double[] b){
	    if(a.length != b.length){
	    	System.out.println("Error: vectors must be the same dimension");
	    	return 0;
	    }
	
		double total = 0;
		for(int i = 0; i < a.length; i++){
			total += a[i] * b[i];
		}
		
		return total;
	}
	
	private void displayInText(Mat img){
		for(int i = 0; i < img.rows(); i += 5){
			for(int j = 0; j < img.cols(); j += 5){
//				System.out.print((img.get(i, j)[0] == 122 && img.get(i, j)[1] == 78 && img.get(i, j)[2] == 49) ? 1 : 0);
				System.out.print((img.get(i, j)[0] > 100 && img.get(i, j)[1] > 100 && img.get(i, j)[2] > 100) ? 1 : 0);
			}
			System.out.println();
		}
		
		System.out.println("\n\n\n");
	}

}
