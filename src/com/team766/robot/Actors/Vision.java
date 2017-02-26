package com.team766.robot.Actors;

import interfaces.CameraInterface;

import java.util.ArrayList;
import java.util.Arrays;

import lib.Actor;
import lib.LogFactory;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.team766.lib.Messages.VisionStatusUpdate;
import com.team766.robot.HardwareProvider;

public class Vision extends Actor{
	
	private final long RUN_TIME = 70;
	
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
	private final double AreaSimularityThresh = 0.25;
	private final double WIDTH_THRESH = 0.7;
	private final double X_OFFSET_THRESH = 10;
	
	private final double RECT_WIDTH = 10.25;
	private final double FOCAL_LENGTH = 1892.8;//2.8; //pixels
	private final double CENTER_X = 320;
	private final double CENTER_Y = 240;

	private double outputDist;
	private double outputAngle;
	
	CameraInterface camServer = HardwareProvider.getInstance().getCameraServer();
	
	@Override
	public void init() {
		outputDist = 0;
		outputAngle = 0;
	}
	
	@Override
	public void run() {
		Mat img = new Mat();
		while(true){
			itsPerSec++;
			sleep(RUN_TIME);
	
			camServer.getFrame(img);
			if(img == null || img.empty())
				continue;
		
			//Begin processing image below

			Mat out = process(img);
			
			if(out == null)
				continue;
			
			camServer.putFrame(out);
			
			LogFactory.getInstance("Vision").printPeriodic("Angle: " + getAngle() + "\tDist: " + getDist(), "VisionUpdate", 200);
			
			sendMessage(new VisionStatusUpdate(getAngle(), getDist()));
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
		
		System.out.println("Angle: " + angle);
						
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
		Imgproc.drawContours(display, contours, -1, new Scalar(0, 255, 0));
		
		MatOfPoint2f[] contours_poly = new MatOfPoint2f[contours.size()];
		for(int i = 0; i < contours_poly.length; i++){
			contours_poly[i] = new MatOfPoint2f();
		}
		Rect[] boundRect = new Rect[contours.size()];
		for(int i = 0; i < boundRect.length; i++){
			boundRect[i] = new Rect();
		}
		
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
				if(Math.abs(boundRect[i].height - boundRect[j].height) < SimularityThresh &&
						Math.abs(boundRect[i].x - boundRect[j].x) < SimularityThresh)
					boundRect[j] = null;
			}
		}	
		System.out.println("Bounded: " + Arrays.toString(boundRect));
		//Check if it should have 3 rects or just 2
		int width = 0;
		
		for(Rect r : boundRect){
			for(Rect j : boundRect){
				if(r == j || r == null || j == null)
					continue;
				if(Math.abs(1.0-Math.abs(Math.max(r.width, j.width) / Math.min(r.width, j.width))) < WIDTH_THRESH)
					width = r.width;
			}
		}
		
		//Remove rects with diff widths
		for(int i = 0; i < boundRect.length; i++){
			if(boundRect[i] == null || width == 0)
				continue;
			if(boundRect[i].width == 0 || Math.abs(1.0-Math.abs(Math.max(width, boundRect[i].width) / Math.min(width, boundRect[i].width))) > WIDTH_THRESH)
				boundRect[i] = null;
		}
		
		boolean pairFound = false;
		//Check to see if there are two rects with same width, and if multiple largest area prioritized
		if(arraySize(boundRect) > 2){
			imporRects[0] = new Rect(0,0,0,0);
			imporRects[1] = new Rect(0,0,0,0);
			for(Rect r : boundRect){
				for(Rect r1 : boundRect){
					if(r == r1 || r == null || r1 == null)
						continue;
					//Same Area: two squares among alot of others
					if((Math.abs(r.height - r1.height) < X_OFFSET_THRESH &&  //Areas have same height
							((Math.abs(1.0 - Math.max(r.area(), r1.area()) / Math.min(r.area(), r1.area()))) < AreaSimularityThresh) && //Areas similar
							(r.area() > imporRects[0].area() && r.area() > imporRects[0].area()))){	//Area bigger than previously found rects
						System.out.println("Same AREA");
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
						if(pairFound || r == r1 || r == r2 || r1 == r2 || r2 == null || r == null || r1 == null)
							continue;
						
						//Check same width and diff x-values
						if(((Math.abs(r.width - r1.width) < X_OFFSET_THRESH && Math.abs(r.width - r2.width) < X_OFFSET_THRESH) && Math.abs(r1.x - r2.x) < X_OFFSET_THRESH) && 
								Math.abs(r.height - (r1.br().y - r2.tl().y)) < X_OFFSET_THRESH){
								
							System.out.println("Same Stuff");
							imporRects[0] = r;
							imporRects[1] = r1;
							imporRects[2] = r2;
							pairFound = true;
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
		
		System.out.println("Important: " + Arrays.toString(imporRects));
		
		if(imporRects[0] != null && imporRects[1] != null){
			Point centerPoint = new Point((imporRects[0].x + imporRects[0].width/2.0  + imporRects[1].x + imporRects[1].width/2.0)/2.0, CENTER_Y);
			outputAngle = Math.toDegrees(convertPointToAngle(centerPoint));
		}
		double maxX = 0;
		double minX = output.width();
		if(imporRects.length > 2){
			maxX = Math.max(Math.max(imporRects[0].br().x, imporRects[1].br().x), imporRects[3].br().x );
			minX = Math.min(Math.min(imporRects[0].br().x, imporRects[1].br().x), imporRects[3].br().x );
		}else if(imporRects.length == 2){
			maxX = Math.max(imporRects[0].br().x, imporRects[1].br().x);
			minX = Math.min(imporRects[0].br().x, imporRects[1].br().x);
		}
		
		//Set values
		outputDist = (RECT_WIDTH * FOCAL_LENGTH) / (maxX - minX);
		
		return display;
	}
	
	private double arraySize(Object[] in){
		int count = 0;
		for(Object i : in){
			if(in != null)
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
