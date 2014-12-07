import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;


public class DataVis {

	public void plotBarChart(TreeMap<String,Integer> data, String title, String xTitle, String yTitle) {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		for(Entry<String,Integer> entry : data.entrySet()) {
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
	        
	        chart.setBackgroundPaint(ChartColor.WHITE);
	        try {
	            ChartUtilities.saveChartAsPNG(new File("chart.png"), chart, 800, 600);
	        } catch(IOException e) {
	            e.printStackTrace();
	        }
	}
	
	public void plotLineChart(TreeMap<Integer,Integer> data, String title, String xTitle, String yTitle) {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		for(Entry<Integer,Integer> entry : data.entrySet()) {
			dataSet.addValue(entry.getValue(), "series", entry.getKey());
		}
		
		JFreeChart chart = ChartFactory.createLineChart(
	            title,       // chart title
	            xTitle,                    // domain axis label
	            yTitle,                   // range axis label
	            dataSet,                   // data
	            PlotOrientation.VERTICAL,  // orientation
	            false,                      // include legend
	            false,                      // tooltips
	            false                      // urls
	        );
		chart.setBackgroundPaint(ChartColor.white);
		
        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(ChartColor.lightGray);
        plot.setRangeGridlinePaint(ChartColor.white);
        
        // customise the range axis...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(true);
        try {
            ChartUtilities.saveChartAsPNG(new File("lineChart.png"), chart, 800, 600);
        } catch(IOException e) {
            e.printStackTrace();
        }
        
	}
	
	public void plotMultipleLineCharts(List<TreeMap<Integer,Integer>> data, String title, String xTitle, String yTitle) {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		for(int i=0; i<data.size(); i++) {
			for(Entry<Integer,Integer> entry : data.get(i).entrySet()) {
				dataSet.addValue(entry.getValue(), "series"+i, entry.getKey());
			}
		}
		
		JFreeChart chart = ChartFactory.createLineChart(
	            title,       // chart title
	            xTitle,                    // domain axis label
	            yTitle,                   // range axis label
	            dataSet,                   // data
	            PlotOrientation.VERTICAL,  // orientation
	            false,                      // include legend
	            false,                      // tooltips
	            false                      // urls
	        );
		chart.setBackgroundPaint(ChartColor.white);
		
        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(ChartColor.lightGray);
        plot.setRangeGridlinePaint(ChartColor.white);
        
        // customise the range axis...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(true);
        try {
            ChartUtilities.saveChartAsPNG(new File("lineChart.png"), chart, 800, 600);
        } catch(IOException e) {
            e.printStackTrace();
        }
        
	}
	
	public static void plotPieChart(TreeMap<String,Integer> data, String title) {
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
	        
	        try {
	            ChartUtilities.saveChartAsPNG(new File("pieChart.png"), chart, 800, 600);
	        } catch(IOException e) {
	            e.printStackTrace();
	        }
	}
	
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

}
