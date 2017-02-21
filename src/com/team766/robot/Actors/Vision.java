package com.team766.robot.Actors;

import interfaces.CameraInterface;

import java.util.ArrayList;

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
		
		double[] sums = new double[output.width()];
		
		for(int i = 0; i < output.width(); i++){
			for(int j = 0; j < output.height(); j++){
				sums[i] += output.get(j, i)[0]/255.0;
			}
		}
		
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(output, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		//More than 3 boxes
		if(contours.size() > 3)
			return null;
		
		MatOfPoint2f[] contours_poly = new MatOfPoint2f[contours.size()];
		for(int i = 0; i < contours_poly.length; i++){
			contours_poly[i] = new MatOfPoint2f();
		}
		Rect[] boundRect = new Rect[contours.size()];
		
		double maxX = 0;
		double minX = output.width();
		
		for( int i = 0; i < contours.size(); i++ ){
			Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contours_poly[i], 3.0, true);
			boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contours_poly[i].toArray()));
			if(boundRect[i].x < minX)
				minX = boundRect[i].x;
			else if(boundRect[i].x + boundRect[i].width > maxX)
				maxX = boundRect[i].x + boundRect[i].width;
		}
		
		//Set values
		outputAngle = Math.toDegrees(convertPointToAngle(new Point((maxX + minX) / 2.0, CENTER_Y)));
		outputDist = (RECT_WIDTH * FOCAL_LENGTH) / (maxX - minX);
		
		return display;
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
