package org.opensourcedea.gui.maingui;

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.opensourcedea.dea.DEAPSolution;
import org.opensourcedea.gui.exceptions.NoVariableException;
import org.opensourcedea.gui.exceptions.UnselectedVariablesException;
import org.opensourcedea.gui.exceptions.UnvalidVariableChoiceException;
import org.opensourcedea.gui.startgui.OSDEA_StatusLine;
import org.opensourcedea.gui.utils.IOManagement;
import org.opensourcedea.gui.utils.Images;
import org.opensourcedea.ldeaproblem.LDEAProblem;

public class Navigation extends Composite {
	
	
	private Tree tree;
	
	private TreeItem deaProblemTreeItem, rawDataTreeItem, variablesTreeItem, modelDetailsTreeItem,
	solutionTreeItem, objectivesTreeItem, projectionsTreeItem, lambdasTreeItem,
	referenceSetTreeItem, slacksTreeItem, weightsTreeItem;
	
	private Image deaProblemImage, rawDataImage, iosImage, deaModelImage, solutionImage,
	objectivesImage, projectionsImage, lambdasImage, referenceSetImage, slacksImage,
	weightsImage;
	
	private final ImageRegistry imgReg;
	
	private OSDEA_StatusLine stl;
	
	private final String rawDataItemText = "Raw Data", variablesItemText = "Variables", modelDetailsItemText = "Model details",
	solutionItemText = "Solution", objectivesItemText = "Objectives", projectionsItemText = "Projections", lambdasItemText = "Lambdas",
	peerGroupItemText = "Peer Group", slacksItemText = "Slacks", weightsItemText = "Weights";
	
	private HashMap<TreeItem, String> filePathHashMap = new HashMap<TreeItem, String>();
	
	private final OSDEAMainComposite osdeaMainComp;
	
	final Composite parent;
	
	
	public Navigation (final Composite parent, OSDEA_StatusLine parentStl) {
		
		super(parent, SWT.BORDER);
		this.parent = parent;
		imgReg = Images.getFullImageRegistry(this.getDisplay());
		deaProblemImage = imgReg.get("deaProblem");
		rawDataImage = imgReg.get("rawData");
		iosImage = imgReg.get("variables");
		deaModelImage = imgReg.get("modelDetails");
		solutionImage = imgReg.get("solution");
		objectivesImage = imgReg.get("objective");
		projectionsImage = imgReg.get("projections");
		
		lambdasImage = imgReg.get("lambdas");
		referenceSetImage = imgReg.get("referenceSet");
		slacksImage = imgReg.get("slacks");
		weightsImage = imgReg.get("weights");
		
		stl = parentStl;
		
		osdeaMainComp = (OSDEAMainComposite)parent;
		
		tree = new Tree(this, SWT.NONE);
		
		tree.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				TreeItem it = (TreeItem)e.item;
				TreeItem parentIt = getSelectedDEAProblemTreeItem();
				
				
				if(parentIt != null){
					
					//Set position stl label
					if(parentIt == it) {
						stl.setPositionLabel(parentIt.getText());
						checkProblemStatus();
					}
					else {
						stl.setPositionLabel(parentIt.getText() + ":" + it.getText());
					}
					
					
					//Set status stl label
					if(it.getText().equals(rawDataItemText)) {
						if(hasData()) {
							stl.setStatusLabel("Data imported.");
						}
						else {
							stl.setStatusLabel("You need to import data.");
						}
					}
					else if(it.getText().equals(variablesItemText)) {
						if(hasData()) {
							((VariablesComposite)getVariableTreeItem().getData()).updateStatusStl();
						}
						else {
							stl.setStatusLabel("You need to import data before you can configure variables.");
						}
					}
					else if(it.getText().equals(modelDetailsItemText)) {
						((ModelDetailsComposite)getModelDetailsTreeItem().getData()).updateStatusStl();
					}
					
					
					
					//Set top composite in stack layout
					Object[] objArr = (Object[])parentIt.getData();

					if(it == parentIt) {
						osdeaMainComp.setDataPanelTopControl((DEAPProblemComposite)objArr[1]);
						
					}
					else{
						osdeaMainComp.setDataPanelTopControl((Control)it.getData());
					}					

				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
			
		});
		
	
		this.setLayout(new FillLayout());
					
	}
	
	private boolean areVarOK() {
		
		boolean areVarOK = true;
		VariablesComposite varComp = (VariablesComposite)getVariableTreeItem().getData();
		try {
			varComp.checkIfVarAreOK();
		}
		catch (UnselectedVariablesException e1) {
			areVarOK = false;
		}
		catch (UnvalidVariableChoiceException e2) {
			areVarOK = false;
		}
		catch (NoVariableException e3) {
			areVarOK = false;
		}

		
		if(areVarOK) {
			return true;
		}
		return false;

	}
	
	public void checkProblemStatus() {
		
		
		DEAPProblemComposite comp = (DEAPProblemComposite)((Object[])getSelectedDEAProblemTreeItem().getData())[1];
		
		
		boolean areVarOK = true;
		boolean areDataOK = false;
		boolean isModelOK = false;
		
		areVarOK = areVarOK();
		
		if(areVarOK) {
			comp.setVariablesOK();
		}
		else {
			comp.setVariablesNOK();
		}
		
		if( ((LDEAProblem)((Object[])getSelectedDEAProblemTreeItem().getData())[0]).getModelDetails().getModelType() !=null )  {
			comp.setModelDetailsOK();
			isModelOK = true;
		}
		else {
			comp.setModelDetailsNOK();
		}
		
		
		if(hasData())  {
			areDataOK = true;
			setDataOK();
		}
		
		if(areVarOK && areDataOK && isModelOK) {
			comp.setAllOK();
			if(isSolved()) {
				stl.setStatusLabel("Problem Solved");
			}
			else {
				stl.setStatusLabel("You are ready to solve");
			}
		}
		else {
			stl.setStatusLabel("You still have a few more things to do");
		}
	}
	
	
	private boolean hasData() {
		if(((LDEAProblem)((Object[])getSelectedDEAProblemTreeItem().getData())[0]).getDataMatrix() != null){
			if( !((LDEAProblem)((Object[])getSelectedDEAProblemTreeItem().getData())[0]).getDataMatrix().isEmpty())  {
				return true;
			}
		}
		return false;
	}
	
	private void refreshVarList(){
		((VariablesComposite)getVariableTreeItem().getData()).refreshVarList();
	}
	
	private void setRawDataTable(LDEAProblem ldeap) {// ArrayList<ArrayList<String>> data) {
		
		((RawDataComposite)getRawDataTreeItem().getData()).setRawDataTable(ldeap);

	}
	
	public TreeItem addDEAProblem(String deaProblemName, LDEAProblem ldeap) {
		
		//Takes time to create (apparently more on first time) => to investigate
		DEAPProblemComposite problemComp = new DEAPProblemComposite(osdeaMainComp.getDataPanel(), ldeap, this, stl);
		RawDataComposite dataComp = null;
		dataComp = new RawDataComposite(osdeaMainComp.getDataPanel(), ldeap);
		
		VariablesComposite variablesComp = new VariablesComposite(osdeaMainComp.getDataPanel(), this, ldeap, stl);
		//Takes time to create (apparently more on first time) => to investigate
		ModelDetailsComposite modelComp = new ModelDetailsComposite(osdeaMainComp.getDataPanel(), ldeap, stl);
		SolutionComposite solutionComp = new SolutionComposite(osdeaMainComp.getDataPanel());
		ObjectivesComposite objectivesComp = new ObjectivesComposite(osdeaMainComp.getDataPanel());
		ProjectionsComposite projectionsComp = new ProjectionsComposite(osdeaMainComp.getDataPanel());
		LambdasComposite lambdasComp = new LambdasComposite(osdeaMainComp.getDataPanel());
		SlacksComposite slacksComp = new SlacksComposite(osdeaMainComp.getDataPanel());
		WeightsComposite weightsComp = new WeightsComposite(osdeaMainComp.getDataPanel());
		
		
		Object[] deaPTreeItemData = new Object[2];
		deaPTreeItemData[0] = ldeap;
		deaPTreeItemData[1] = problemComp;
		
		deaProblemTreeItem = new TreeItem (tree, 0);
		deaProblemTreeItem.setText (deaProblemName);
		deaProblemTreeItem.setImage(deaProblemImage);
		deaProblemTreeItem.setData(deaPTreeItemData);
		
		rawDataTreeItem = new TreeItem (deaProblemTreeItem, 0);
		rawDataTreeItem.setText (rawDataItemText);
		rawDataTreeItem.setImage(rawDataImage);
		rawDataTreeItem.setData(dataComp);
		
		variablesTreeItem = new TreeItem (deaProblemTreeItem, 0);
		variablesTreeItem.setText (variablesItemText);
		variablesTreeItem.setImage(iosImage);
		variablesTreeItem.setData(variablesComp);
		
		modelDetailsTreeItem = new TreeItem (deaProblemTreeItem, 0);
		modelDetailsTreeItem.setText (modelDetailsItemText);
		modelDetailsTreeItem.setImage(deaModelImage);
		modelDetailsTreeItem.setData(modelComp);
		
		
		solutionTreeItem = new TreeItem (deaProblemTreeItem, 0);
		solutionTreeItem.setText (solutionItemText);
		solutionTreeItem.setImage(solutionImage);
		solutionTreeItem.setData(solutionComp);
		
		
		objectivesTreeItem = new TreeItem (solutionTreeItem, 0);
		objectivesTreeItem.setText (objectivesItemText);
		objectivesTreeItem.setImage(objectivesImage);
		objectivesTreeItem.setData(objectivesComp);
		
		
		projectionsTreeItem = new TreeItem (solutionTreeItem, 0);
		projectionsTreeItem.setText (projectionsItemText);
		projectionsTreeItem.setImage(projectionsImage);
		projectionsTreeItem.setData(projectionsComp);
		
		
		lambdasTreeItem = new TreeItem (solutionTreeItem, 0);
		lambdasTreeItem.setText (lambdasItemText);
		lambdasTreeItem.setImage(lambdasImage);
		lambdasTreeItem.setData(lambdasComp);
		
		
		referenceSetTreeItem = new TreeItem (solutionTreeItem, 0);
		referenceSetTreeItem.setText (peerGroupItemText);
		referenceSetTreeItem.setImage(referenceSetImage);
		referenceSetTreeItem.setData(lambdasComp);
		
		slacksTreeItem = new TreeItem (solutionTreeItem, 0);
		slacksTreeItem.setText (slacksItemText);
		slacksTreeItem.setImage(slacksImage);
		slacksTreeItem.setData(slacksComp);
		
		
		weightsTreeItem = new TreeItem (solutionTreeItem, 0);
		weightsTreeItem.setText (weightsItemText);
		weightsTreeItem.setImage(weightsImage);
		weightsTreeItem.setData(weightsComp);

		
		fireSelectionChange(deaProblemTreeItem);
		
		if(ldeap.isSolved()){
			displaySolution();
		}
		
		return deaProblemTreeItem;
		
	}
	
	/*
	 * Used by listeners when creating new problems
	 */
	public void addDEAProblem(String deaProblemName) {

		LDEAProblem ldeap = new LDEAProblem();
		ldeap.setModelName(deaProblemName);
		
		addDEAProblem(deaProblemName, ldeap);
		
	}
	
	public void closeDEAProblem() {

		TreeItem ti = getSelectedDEAProblemTreeItem();
		
		ti.dispose();
		
		if(getAllTreeItems().length == 0){
			osdeaMainComp.setDataPanelTopControlAsInstr();
		}
		
		stl.resetAllLabels();
		
	}

	
	
	/*
	 * Used by DEA Problem Composite when setting problem name
	 */
	public void setDEAProblemText(String str) {

		setDEAProblemText(str, getSelectedDEAProblemTreeItem());
		
	}
	
	public void setDEAProblemText(String str, TreeItem ti) {
		if(ti != null) {
			TreeItem deapIt = ti;
			deapIt.setText(str);
			stl.setPositionLabel(str);
			if(getFilePath() != null) {
				String newFilePath = IOManagement.getNewFileName(getFilePath(ti), str);
				updateFilePath(newFilePath, ti);
			}
		}
	}
	
	public void setModelLDEAProblemName(String newModelName) {
		LDEAProblem ldeap = getSelectedDEAProblem();
		if(ldeap != null) {
			ldeap.setModelName(newModelName);
		}
	}
	
	
	private TreeItem getSelectedDEAProblemTreeItem() {
		if(tree.getSelectionCount() > 0) {
			TreeItem[] it = tree.getSelection();
			TreeItem deapIt = it[0];
			while(deapIt.getParentItem() != null) {
				deapIt = deapIt.getParentItem();
			}
			return deapIt;
		}
		return null;
	}
	
	public TreeItem[] getAllTreeItems() {
		
		return tree.getItems();	
	}
	
	
	
	private boolean isSolved() {
		if(getSelectedDEAProblem().isSolved()) {
			return true;
		}
		return false;
	}
	
	public LDEAProblem getSelectedDEAProblem() {
		
		try {
			return getLDEAProblem(getSelectedDEAProblemTreeItem());
		}
		catch (Exception e) {
			return null;
		}
	
	}
	
	
	public LDEAProblem getLDEAProblem(TreeItem ti) {
		return (LDEAProblem)((Object[])ti.getData())[0];
	}
	
	
	public TreeItem getSelectedTreeItem() {
		return getSelectedDEAProblemTreeItem();
	}

	

	
	private TreeItem getRawDataTreeItem() {
		TreeItem parent = getSelectedDEAProblemTreeItem();
		for(TreeItem it : parent.getItems()) {
			if(it.getText().equals(rawDataItemText)) {
				return it;
			}
		}
		return null;
	}
	
	private TreeItem getVariableTreeItem() {
		TreeItem parent = getSelectedDEAProblemTreeItem();
		for(TreeItem it : parent.getItems()) {
			if(it.getText().equals(variablesItemText)) {
				return it;
			}
		}
		return null;
	}
	
	private TreeItem getModelDetailsTreeItem() {
		TreeItem parent = getSelectedDEAProblemTreeItem();
		for(TreeItem it : parent.getItems()) {
			if(it.getText().equals(modelDetailsItemText)) {
				return it;
			}
		}
		return null;
	}
	
	private TreeItem getObjectivesTreeItem() {
		TreeItem parent = getSelectedDEAProblemTreeItem();
		
		for(TreeItem it : parent.getItems()) {
			if(it.getText().equals(solutionItemText)) {
				for(TreeItem innerIt : it.getItems()) {
					if(innerIt.getText().equals(objectivesItemText)) {
						return innerIt;
					}
				}
			}
		}
		
		return null;
	}
	
	private TreeItem getProjectionsTreeItem() {
		TreeItem parent = getSelectedDEAProblemTreeItem();
		
		for(TreeItem it : parent.getItems()) {
			if(it.getText().equals(solutionItemText)) {
				for(TreeItem innerIt : it.getItems()) {
					if(innerIt.getText().equals(projectionsItemText)) {
						return innerIt;
					}
				}
			}
		}
		
		return null;
	}
	
	private TreeItem getLambdasTreeItem() {
		TreeItem parent = getSelectedDEAProblemTreeItem();
		
		for(TreeItem it : parent.getItems()) {
			if(it.getText().equals(solutionItemText)) {
				for(TreeItem innerIt : it.getItems()) {
					if(innerIt.getText().equals(lambdasItemText)) {
						return innerIt;
					}
				}
			}
		}
		
		return null;
	}
	
	private TreeItem getSlacksTreeItem() {
		TreeItem parent = getSelectedDEAProblemTreeItem();
		
		for(TreeItem it : parent.getItems()) {
			if(it.getText().equals(solutionItemText)) {
				for(TreeItem innerIt : it.getItems()) {
					if(innerIt.getText().equals(slacksItemText)) {
						return innerIt;
					}
				}
			}
		}
		
		return null;
	}
	
	private TreeItem getWeightsTreeItem() {
		TreeItem parent = getSelectedDEAProblemTreeItem();
		
		for(TreeItem it : parent.getItems()) {
			if(it.getText().equals(solutionItemText)) {
				for(TreeItem innerIt : it.getItems()) {
					if(innerIt.getText().equals(weightsItemText)) {
						return innerIt;
					}
				}
			}
		}
		
		return null;
	}

	
	
	private void setDataOK() {
		DEAPProblemComposite comp = (DEAPProblemComposite)((Object[])getSelectedDEAProblemTreeItem().getData())[1];
		comp.setDataOK();
	}
	
	
	/*
	 * used by the import file wizard. Allows the wizard to only read data and present it to the nav via this methods (thus the
	 * wizard doesn't know about the inner structure of the program).
	 */
	public void importData(ArrayList<double[]> dataMatrix, ArrayList<String> dmuNames, ArrayList<String> variableNames) { //, ArrayList<ArrayList<String>> fullDataMatrixString) {
		
		boolean backupProblem = false;
		LDEAProblem ldeap = null;
		ArrayList<double[]> backupDataMatrix = null;
		ArrayList<String> backupDMUNames = null;
		ArrayList<String> backupVariableNames = null;
		
		
		try {
			ldeap = (LDEAProblem)((Object[])getSelectedDEAProblemTreeItem().getData())[0];
			if(ldeap.getDMUNames() !=null && ldeap.getDataMatrix() != null && ldeap.getVariableNames() != null) {
				backupDataMatrix = ldeap.getDataMatrix();
				backupDMUNames = ldeap.getDMUNames();
				backupVariableNames = ldeap.getVariableNames();
				
			}
		} catch (Exception e) {
			backupProblem = true;
		}
		
		
		if(!backupProblem) {
			try {
				
				ArrayList<String> headers = new ArrayList<String>();
				headers.add("DMU Names");
				headers.addAll(variableNames);
				
				ldeap.setDataMatrix(dataMatrix);
				ldeap.setDMUNames(dmuNames);
				ldeap.setVariableNames(variableNames);
				refreshVarList();
				setRawDataTable(ldeap);
				setDataOK();
				ldeap.setModified(true);
				stl.setNotificalLabelDelayStandard("Successful Imported " + ldeap.getDMUNames().size() + " DMUs, " + ldeap.getVariableNames().size() + " Variables.");
			}
			catch (Exception e) {
				ldeap.setDataMatrix(backupDataMatrix);
				ldeap.setDMUNames(backupDMUNames);
				ldeap.setVariableNames(backupVariableNames);
				MessageDialog.openWarning(this.getShell(),
						"Warning", "The data file to import could be read correctly but could not be displayed.\n" +
						"This should not happen so is likely a bug. Please report it so we can fix it!");
			}
		}

	}
	
	
	
	
	public void displaySolution() {
		LDEAProblem ldeap = getSelectedDEAProblem();
		
		//Objectives
		ObjectivesComposite objComp = (ObjectivesComposite)getObjectivesTreeItem().getData();
		objComp.displaySolution(ldeap);
		
		//Projections
		ProjectionsComposite projComp = (ProjectionsComposite)getProjectionsTreeItem().getData();
		projComp.displaySolution(ldeap);
		
		//Lambdas
		LambdasComposite lambdaComp = (LambdasComposite)getLambdasTreeItem().getData();
		lambdaComp.displaySolution(ldeap);
		
		
		//Slacks
		SlacksComposite slackComp = (SlacksComposite)getSlacksTreeItem().getData();
		slackComp.displaySolution(ldeap);
		
		//Weight
		WeightsComposite weightsComp = (WeightsComposite)getWeightsTreeItem().getData();
		weightsComp.displaySolution(ldeap);
		
	}
	
	public void deleteSolution() {
		
		
		
		LDEAProblem ldeap = getSelectedDEAProblem();
		
		DEAPSolution backup = ldeap.getLdeapSolution();
		
		boolean modified = ldeap.isModified();
		
		
		try {
			ldeap.deleteSolution();

			//Objectives
			ObjectivesComposite objComp = (ObjectivesComposite)getObjectivesTreeItem().getData();
			objComp.resetComposite();

			//Projections
			ProjectionsComposite projComp = (ProjectionsComposite)getProjectionsTreeItem().getData();
			projComp.resetComposite();

			//Lambdas
			LambdasComposite lambdaComp = (LambdasComposite)getLambdasTreeItem().getData();
			lambdaComp.resetComposite();		

			//Slacks
			SlacksComposite slackComp = (SlacksComposite)getSlacksTreeItem().getData();
			slackComp.resetComposite();

			//Weight
			WeightsComposite weightsComp = (WeightsComposite)getWeightsTreeItem().getData();
			weightsComp.resetComposite();

			ldeap.setSolved(false);
			ldeap.setModified(true);

			stl.setNotificalLabelDelayStandard("Problem reset successfully.");
		}
		catch (Exception e) {
			
			ldeap.setLdeapSolution(backup);
			displaySolution();
			ldeap.setSolved(true);
			ldeap.setModified(modified);
			
			getDisplay().syncExec(new Runnable() {
				public void run() {
					MessageDialog.openWarning(getShell(), "Reset error", "The problem" +
							"could not be reset! Previous solution was restored.");
				}
			});
			stl.setNotificalLabelDelayStandard("There was a problem resetting the solution.");
			stl.setStatusLabel("Problem Solved");
		}
		
	}
	
	
	
	public void addFilePath(String fullFilePath) {
		addFilePath(fullFilePath, getSelectedDEAProblemTreeItem());
	}
	
	public void addFilePath(String fullFilePath, TreeItem ti) {
		filePathHashMap.put(ti, fullFilePath);
	}
	
	public String getFilePath() {
		
		return getFilePath(getSelectedDEAProblemTreeItem());
		
	}
	
	public String getFilePath(TreeItem ti) {
		if(filePathHashMap.containsKey(ti)) {
			return filePathHashMap.get(ti);
		}
		return null;
	}
	
	public void updateFilePath(String newFilePath) {
		updateFilePath(newFilePath, getSelectedDEAProblemTreeItem());
	}
	
	public void updateFilePath(String newFilePath, TreeItem ti) {
		filePathHashMap.put(ti, newFilePath);
	}
	
	
	private void fireSelectionChange(TreeItem item) {
		Event event = new Event();
		event.item = item;
		tree.select(item);
		tree.notifyListeners(SWT.Selection, event);
	}
	
}
