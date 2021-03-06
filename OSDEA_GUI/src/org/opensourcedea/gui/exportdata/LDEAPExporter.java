package org.opensourcedea.gui.exportdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.opensourcedea.dea.ReturnsToScale;
import org.opensourcedea.dea.VariableOrientation;
import org.opensourcedea.gui.utils.IOManagement;
import org.opensourcedea.ldeaproblem.LDEAProblem;

public class LDEAPExporter {

	private LDEAProblem ldeap;
	private Shell shell;
	private static final String[] filterNames = {
		"Excel 2007 File (*.xls)",
		"Excel 2010 File (*.xlsx)"
	};
	private static final String[] filterExts = {
		"*.xls",
		"*.xlsx"
	};


	public LDEAPExporter(LDEAProblem ldeap, Shell shell) {
		this.ldeap = ldeap;
		this.shell = shell;

	}


	public boolean exportToFile() throws IOException {

		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterNames(filterNames);
		dialog.setFilterExtensions(filterExts);
		if(ldeap.getModelName() != ""){
			dialog.setFileName(ldeap.getModelName());// + ".xls");
		}


		String fileName = null;

		boolean dontCare = false;
		while(!dontCare) {
			fileName = dialog.open();
			if (fileName == null) {
				return false;
			}
			File f = new File(fileName);
			if(f.exists()){
				if(MessageDialog.openQuestion(shell, "Overwrite", "The file already exists. Do you want to overwrite it?") == 
						true) {
					dontCare = true;
				}
			}else{
				dontCare = true;
			}
		}


		if(fileName != null) {

			if(IOManagement.getExtension(fileName).equals("xls")) {
				FileOutputStream out = new FileOutputStream(fileName);
				Workbook wb = new HSSFWorkbook();
				buildFile(wb);			
				wb.write(out);
				out.close();
			}
			else {
				FileOutputStream out = new FileOutputStream(fileName);
				Workbook wb = new XSSFWorkbook();
				buildFile(wb);			
				wb.write(out);
				out.close();
			}

		}

		return true;
	}


	private void buildFile(Workbook wb) {

		//Sheet order MATTERS! (for the setSheetName methods)

		//Model Details
		exportModelDetails(wb);
		wb.setSheetName(0, "Model details");

		//Raw Data
		exportRawData(wb);
		wb.setSheetName(1, "Raw Data");

		//Variables
		exportVariables(wb);
		wb.setSheetName(2, "Variables");


		if(ldeap.isSolved()) {
			//Objectives
			exportObjectives(wb);
			wb.setSheetName(3, "Objectives");

			//Projections
			exportProjections(wb);
			wb.setSheetName(4, "Projections");

			//Lambdas
			exportLambdas(wb);
			wb.setSheetName(5, "Lambdas");


			//Peer Group
			exportPeerGroup(wb);
			wb.setSheetName(6, "Peer Group");

			//Slacks
			exportSlacks(wb);
			wb.setSheetName(7, "Slacks");

			//Weights
			exportWeights(wb);
			wb.setSheetName(8, "Weights");
		}


	}


	private void exportModelDetails(Workbook wb) {
		Sheet modelDetailsSheet = wb.createSheet();

		Row row = null;
		Cell cell = null; 

		row = modelDetailsSheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("Model Name");
		cell = row.createCell(1);
		cell.setCellValue(ldeap.getModelName());

		row = modelDetailsSheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue("Model Type");
		cell = row.createCell(1);
		cell.setCellValue(ldeap.getModelType().getName().toString());

		row = modelDetailsSheet.createRow(2);
		cell = row.createCell(0);
		cell.setCellValue("Model Orientation");
		cell = row.createCell(1);
		cell.setCellValue(ldeap.getModelType().getOrientation().toString());

		row = modelDetailsSheet.createRow(3);
		cell = row.createCell(0);
		cell.setCellValue("Model Efficiency Type");
		cell = row.createCell(1);
		cell.setCellValue(ldeap.getModelType().getEfficiencyType().toString());

		row = modelDetailsSheet.createRow(4);
		cell = row.createCell(0);
		cell.setCellValue("Model RTS");
		cell = row.createCell(1);
		cell.setCellValue(ldeap.getModelType().getReturnToScale().toString());

		row = modelDetailsSheet.createRow(5);
		cell = row.createCell(0);
		cell.setCellValue("Model Description");
		cell = row.createCell(1);
		cell.setCellValue(ldeap.getModelType().getDescription());
	

		if(ldeap.getModelType().getReturnToScale() != ReturnsToScale.CONSTANT &&
				ldeap.getModelType().getReturnToScale() != ReturnsToScale.VARIABLE) {

			row = modelDetailsSheet.createRow(6);
			cell = row.createCell(0);
			cell.setCellValue("Return To Scale Lower Bound");
			cell = row.createCell(1);
			cell.setCellValue(Double.toString(ldeap.getModelDetails().getRtsLB()));

			row = modelDetailsSheet.createRow(7);
			cell = row.createCell(0);
			cell.setCellValue("Return To Scale Upper Bound");
			cell = row.createCell(1);
			cell.setCellValue(Double.toString(ldeap.getModelDetails().getRtsUB()));

		}

		
		//autoSize
		for(int i = 0; i < 2; i++) {
			modelDetailsSheet.autoSizeColumn(i);
		}
		

	}


	private void exportRawData(Workbook wb) {
		Sheet dataSheet = wb.createSheet();

		Row row = null;
		Cell cell = null; 

		row = dataSheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("DMU Name");

		for(int i = 0; i < ldeap.getVariableNames().size(); i++) {
			cell = row.createCell(i + 1);
			cell.setCellValue(ldeap.getVariableNames().get(i));
		}

		for(int rowNum = 0; rowNum < ldeap.getDataMatrix().size(); rowNum++) {

			row = dataSheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue(ldeap.getDMUNames().get(rowNum));

			for(int cellNum = 0; cellNum < ldeap.getDataMatrix().get(0).length; cellNum++) {
				cell = row.createCell(cellNum + 1);
				cell.setCellValue(ldeap.getDataMatrix().get(rowNum)[cellNum]);

			}
		}
		
		//autoSize
		for(int i = 0; i < ldeap.getDataMatrix().get(0).length + 1; i++) {
			dataSheet.autoSizeColumn(i);
		}
		
	}


	private void exportVariables(Workbook wb) {
		Sheet varSheet = wb.createSheet();

		Row row = null;
		Cell cell = null; 

		row = varSheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("Variable Name");
		cell = row.createCell(1);
		cell.setCellValue("Variable Orientation");
		cell = row.createCell(2);
		cell.setCellValue("Variable Type");

		for(int rowNum = 0; rowNum < ldeap.getNumberOfVariables(); rowNum++) {
			row = varSheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue(ldeap.getVariableNames().get(rowNum));
			cell = row.createCell(1);
			if (ldeap.getVariableOrientation().get(rowNum) == null) {
				cell.setCellValue("Variable Not Configured");
			}
			else {
				cell.setCellValue(ldeap.getVariableOrientation().get(rowNum).toString());
			}
			cell = row.createCell(2);
			if (ldeap.getVariableOrientation().get(rowNum) == null) {
				cell.setCellValue("Variable Not Configured");
			}
			else {
				cell.setCellValue(ldeap.getVariableType().get(rowNum).toString());
			}
		}
		
		//autoSize
		for(int i = 0; i < 3; i++) {
			varSheet.autoSizeColumn(i);
		}
		
	}

	private void exportObjectives(Workbook wb) {
		Sheet objSheet = wb.createSheet();

		Row row = null;
		Cell cell = null; 

		row = objSheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("DMU Name");
		cell = row.createCell(1);
		cell.setCellValue("Objective Value");
		cell = row.createCell(2);
		cell.setCellValue("Efficient");

		for(int rowNum = 0; rowNum < ldeap.getLdeapSolution().getObjectives().length; rowNum++) {
			row = objSheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue(ldeap.getDMUNames().get(rowNum));
			cell = row.createCell(1);
			cell.setCellValue(ldeap.getLdeapSolution().getObjective(rowNum));
			cell = row.createCell(2);
			String eff = ldeap.getLdeapSolution().getEfficient()[rowNum] ? "Yes" : "";
			cell.setCellValue(eff);
		}
		
		//autoSize
		for(int i = 0; i < 3; i++) {
			objSheet.autoSizeColumn(i);
		}
		
	}


	private void exportProjections(Workbook wb) {
		Sheet projSheet = wb.createSheet();

		Row row = null;
		Cell cell = null; 
		String[] headers = getVariablesHeaders();
		row = projSheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(headers[i].toString());
		}

		for(int rowNum = 0; rowNum < ldeap.getLdeapSolution().getProjections().length; rowNum++) {
			row = projSheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue(ldeap.getDMUNames().get(rowNum));
			for(int cellNum = 0; cellNum < ldeap.getLdeapSolution().getProjections()[0].length; cellNum++) {
				cell = row.createCell(cellNum + 1);
				cell.setCellValue(ldeap.getLdeapSolution().getProjections(rowNum)[cellNum]);
			}
		}
		
		//autoSize
		for(int i = 0; i < ldeap.getNumberOfVariables() + 1; i++) {
			projSheet.autoSizeColumn(i);
		}
		
	}


	
	
	private void exportLambdas(Workbook wb) {

		Sheet lambdaSheet = wb.createSheet();

		Row row = null;
		Cell cell = null; 
		row = lambdaSheet.createRow(0);

		ArrayList<Integer> efficientReferencedDMUs = ldeap.getEfficientReferencedDMUs();

		int rowi = 1;

		cell = row.createCell(0);
		cell.setCellValue("DMU Name");
		Iterator<Integer> it = efficientReferencedDMUs.iterator();
		while(it.hasNext()) {
			cell = row.createCell(rowi++);
			cell.setCellValue(ldeap.getDMUNames().get(it.next()));
		}

		ArrayList<ArrayList<Double>> data = ldeap.getProcessedLambdas();
		for(int rowNum = 0; rowNum < data.size(); rowNum++) {
			row = lambdaSheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue(ldeap.getDMUNames().get(rowNum));
			int l = 1;
			Iterator<Double> itd = data.get(rowNum).iterator();
			while(itd.hasNext()) {
				cell = row.createCell(l++);
				cell.setCellValue(itd.next());
			}
		}
		
		//autoSize
		for(int i = 0; i < efficientReferencedDMUs.size() + 1 + 1; i++) {
			lambdaSheet.autoSizeColumn(i);
		}
		
	}


	private void exportPeerGroup(Workbook wb) {
		Sheet pgSheet = wb.createSheet();

		Row row = null;
		Cell cell = null; 
		row = pgSheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("DMU Name");
		cell = row.createCell(1);
		cell.setCellValue("Peer Group");

		ArrayList<ArrayList<String>> peerGroup = ldeap.getPeerGroup();

		for(int rowNum = 0; rowNum < peerGroup.size(); rowNum++) {
			row = pgSheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue(peerGroup.get(rowNum).get(0));
			cell = row.createCell(1);
			cell.setCellValue(peerGroup.get(rowNum).get(1));
		}
		
		//autoSize
		for(int i = 0; i < 2; i++) {
			pgSheet.autoSizeColumn(i);
		}
		
	}
	
	/**
	 * Return the headers for variables related results.
	 * @return String[] of Selected Variable Names.
	 */
	private String[] getVariablesHeaders() {
		int nbOfSelVariables = ldeap.getLdeapSolution().getSlacks()[0].length;
		String [] selVarNames = new String[nbOfSelVariables + 1];
		selVarNames[0] = "DMU Names";
		int pos = 1;
		
		for(int j = 0; j < ldeap.getVariableOrientation().size();j++){
			if (ldeap.getVariableOrientation().get(j) == VariableOrientation.INPUT) {
				switch (ldeap.getVariableType().get(j)) {
				case STANDARD:
					selVarNames[pos] = ldeap.getVariableNames().get(j).toString() + " (I)";
					break;
				case NON_CONTROLLABLE:
					selVarNames[pos] = ldeap.getVariableNames().get(j).toString() + " (NC-I)";
					break;
				case NON_DISCRETIONARY:
					selVarNames[pos] = ldeap.getVariableNames().get(j).toString() + " (ND-I)";
					break;
				}
				pos++;
			}
			if (ldeap.getVariableOrientation().get(j) == VariableOrientation.OUTPUT){
				switch (ldeap.getVariableType().get(j)) {
				case STANDARD:
					selVarNames[pos] = ldeap.getVariableNames().get(j).toString() + " (O)";
					break;
				case NON_CONTROLLABLE:
					selVarNames[pos] = ldeap.getVariableNames().get(j).toString() + " (NC-O)";
					break;
				case NON_DISCRETIONARY:
					selVarNames[pos] = ldeap.getVariableNames().get(j).toString() + " (ND-O)";
					break;
				}
				pos++;
			}
		}
		
		
		return selVarNames;
	}

	private void exportSlacks(Workbook wb) {
		Sheet slacksSheet = wb.createSheet();

		Row row = null;
		Cell cell = null;
		String[] headers = getVariablesHeaders();
		row = slacksSheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(headers[i].toString());
		}

		for(int rowNum = 0; rowNum < ldeap.getLdeapSolution().getSlacks().length; rowNum++) {
			row = slacksSheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue(ldeap.getDMUNames().get(rowNum));
			for(int i = 0; i < ldeap.getLdeapSolution().getSlacks()[0].length; i++) {
				cell = row.createCell(i + 1);
				cell.setCellValue(ldeap.getLdeapSolution().getSlack(rowNum, i));
			}
		}
		
		//autoSize
		for(int i = 0; i < ldeap.getNumberOfVariables() + 1; i++) {
			slacksSheet.autoSizeColumn(i);
		}
		
	}

	private void exportWeights(Workbook wb) {
		Sheet weightsSheet = wb.createSheet();

		Row row = null;
		Cell cell = null; 
		String[] headers = getVariablesHeaders();
		row = weightsSheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(headers[i].toString());
		}

		for(int rowNum = 0; rowNum < ldeap.getLdeapSolution().getWeights().length; rowNum++) {
			row = weightsSheet.createRow(rowNum + 1);
			cell = row.createCell(0);
			cell.setCellValue(ldeap.getDMUNames().get(rowNum));
			for(int i = 0; i < ldeap.getLdeapSolution().getWeights()[0].length; i++) {
				cell = row.createCell(i + 1);
				cell.setCellValue(ldeap.getLdeapSolution().getWeight(rowNum, i));
			}
		}
		
		//autoSize
		for(int i = 0; i < ldeap.getNumberOfVariables() + 1; i++) {
			weightsSheet.autoSizeColumn(i);
		}
		
	}


}

