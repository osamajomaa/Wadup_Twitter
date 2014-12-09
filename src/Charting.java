import java.awt.BasicStroke;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Charting {

	Paint[] COLORS = {ChartColor.DARK_MAGENTA, ChartColor.DARK_YELLOW, ChartColor.DARK_RED, ChartColor.ORANGE, 
							ChartColor.BLUE, ChartColor.GREEN};
	
	public void plotBarChart(Map<String, Integer> data, String title, String xTitle, String yTitle, String chartName) {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		for(Entry<String, Integer> entry : data.entrySet()) {
			dataSet.addValue(entry.getValue(), "series", entry.getKey());
		}
		JFreeChart chart = ChartFactory.createBarChart(
	            title,
	            xTitle,
	            yTitle,
	            dataSet,
	            PlotOrientation.VERTICAL,
	            false,
	            false,
	            false
	        );
		
        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        CategoryAxis xAxis = (CategoryAxis)plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        xAxis.setAxisLinePaint(ChartColor.BLACK);
        xAxis.setLabelPaint(ChartColor.BLACK);
        xAxis.setAxisLineStroke(new BasicStroke(2));
        
        Axis yAxis = plot.getRangeAxis();
        yAxis.setAxisLinePaint(ChartColor.BLACK);
        yAxis.setLabelPaint(ChartColor.BLACK);
        yAxis.setAxisLineStroke(new BasicStroke(2));
        
        Random r = new Random();
		int randColor = r.nextInt(COLORS.length-0)+0;	
        
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, COLORS[randColor]);
        chart.setBackgroundPaint(ChartColor.WHITE);
        
        try {
            ChartUtilities.saveChartAsPNG(new File(chartName), chart, 800, 600);
        } catch(IOException e) {
            e.printStackTrace();
        }
	}
	
	public void plotLineChart(Map<Integer,Integer> data, String title, String xTitle, String yTitle, String chartName) {
		//DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		XYSeries dataSet = new XYSeries("Data");
		for(Entry<Integer,Integer> entry : data.entrySet()) {
			dataSet.add(entry.getValue(),entry.getKey());
		}
		XYSeriesCollection coll = new XYSeriesCollection(dataSet);
		JFreeChart chart = ChartFactory.createXYLineChart(
	            title,       				// chart title
	            xTitle,                    // domain axis label
	            yTitle,                   // range axis label
	            coll,                   // data
	            PlotOrientation.VERTICAL,  // orientation
	            false,                      // include legend
	            false,                      // tooltips
	            false                      // urls
	        );
		chart.setBackgroundPaint(ChartColor.white);
		
        final XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(ChartColor.white);
        plot.setRangeGridlinePaint(ChartColor.BLACK);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseStroke(new BasicStroke(4));
        plot.setRenderer(renderer);
        // customise the range axis...
        final NumberAxis xAxis = (NumberAxis) plot.getRangeAxis();
        xAxis.setTickUnit(new NumberTickUnit(5));

        // Create an NumberAxis
        try {
            ChartUtilities.saveChartAsPNG(new File(chartName), chart, 800, 600);
        } catch(IOException e) {
            e.printStackTrace();
        }
        
	}
	
	public void plotMultipleLineCharts(List<HashMap<Integer,Integer>> data, String title, String xTitle, String yTitle,
										String chartName) {
		XYSeriesCollection coll = new XYSeriesCollection();
		for(int i=0; i<data.size(); i++) {
			XYSeries dataSet = new XYSeries("Data"+i);
			for(Entry<Integer,Integer> entry : data.get(i).entrySet()) {
				dataSet.add(entry.getValue(),entry.getKey());
			}
			coll.addSeries(dataSet);
		}
		
		JFreeChart chart = ChartFactory.createXYLineChart(
	            title,       // chart title
	            xTitle,                    // domain axis label
	            yTitle,                   // range axis label
	            coll,                   // data
	            PlotOrientation.VERTICAL,  // orientation
	            false,                      // include legend
	            false,                      // tooltips
	            false                      // urls
	        );
		chart.setBackgroundPaint(ChartColor.white);
		
        final XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(ChartColor.white);
        plot.setRangeGridlinePaint(ChartColor.BLACK);
        
        // customise the range axis...
        final NumberAxis xAxis = (NumberAxis) plot.getRangeAxis();
        xAxis.setTickUnit(new NumberTickUnit(5));
        
        // Create an NumberAxis
        try {
            ChartUtilities.saveChartAsPNG(new File(chartName), chart, 800, 600);
        } catch(IOException e) {
            e.printStackTrace();
        }
        
	}
	
	public void plotPieChart(Map<String,Integer> data, String title, String chartName) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for(Entry<String,Integer> entry : data.entrySet()) {
			dataset.setValue(entry.getKey(), entry.getValue());
		}
		JFreeChart chart = ChartFactory.createPieChart(
	            title,  // chart title
	            dataset,             // data
	            true,               // include legend
	            true,
	            false
	        );

	        PiePlot plot = (PiePlot) chart.getPlot();
	        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
	        plot.setNoDataMessage("No data available");
	        plot.setCircular(false);
	        plot.setLabelGap(0.02);
	        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
	                "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0.0%"));
	            plot.setLabelGenerator(gen);
	        
	        try {
	            ChartUtilities.saveChartAsPNG(new File(chartName), chart, 800, 600);
	        } catch(IOException e) {
	            e.printStackTrace();
	        }
	}
	
	//Won't work with too many markers
	public void plotMap(List<String> locations) {
		StringBuffer url = new StringBuffer("https://maps.googleapis.com/maps/api/staticmap?");
		url.append("center=0");
		url.append("&size=800x400");
		url.append("&scale=2");
		url.append("&maptype=roadmap");
		url.append("&format=png");
		url.append("&zoom=1");
		for(String loc : locations) {
			url.append("&markers=color:red%7C"+loc);
		}
		System.out.println(url.toString());
		try {
			
		    BufferedImage img = ImageIO.read(new URL(url.toString()));
		    File outputfile = new File("map.png");
		    ImageIO.write(img, "png", outputfile);
		    System.out.println("Saved!");
		    } catch (Exception ex) {
		         System.out.println("Error!" + ex);
		    }
	}
	
	public void openGMaps(List<String> locations) {
		StringBuffer htmlPage = new StringBuffer();
		for(String loc : locations) {
			htmlPage.append("["+loc.split(",")[0]+","+loc.split(",")[1]+"],");
		}
		String top="", bottom="";
		try {
			top = new Scanner(new File("utils/maps_page_top")).useDelimiter("\\Z").next();
			bottom = new Scanner(new File("utils/maps_page_bottom")).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			System.out.println("Failed to read from top or bottom files! No maps will show up");
			return;
		}
		htmlPage.insert(0, top);
		htmlPage.append(bottom);
		File mapFile = new File("temp/maps.html");
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(mapFile));
			writer.write(htmlPage.toString());
			writer.close();
		} catch (IOException e) {
			System.out.println("Failed to write to HTML file! No maps will show up");
			return;
		}
		try {
			Desktop.getDesktop().browse(mapFile.toURI());
		} catch (IOException e) {
			System.out.println("Failed to open browser! No maps will show up");
			return;
		}
	}

}
