/*
 * $Id: DetailedSearch.java,v 1.1 2005/03/20 11:02:29 eiki Exp $ Created on Mar 16, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package se.agura.search.presentation;

import java.rmi.RemoteException;
import se.agura.applications.business.ApplicationsBusiness;
import se.agura.search.SearchConstants;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.search.presentation.SearchResults;
import com.idega.core.search.presentation.Searcher;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.util.SelectorUtility;
import com.idega.user.presentation.UserStatusDropdown;

public class DetailedSearch extends Block implements SearchConstants{

	public static final String IW_BUNDLE_IDENTIFIER = "se.agura";

	private String textStyleClass;
	private String headerStyleClass;
	private String inputStyleClass;
	private String buttonStyleClass;

	public DetailedSearch() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void main(IWContext iwc) throws Exception {
		addContactSearch(iwc);
		addBreak();
		addDocumentSearch(iwc);
		addBreak();
		addResults(iwc);
	}

	private void addResults(IWContext iwc) {
		SearchResults results = new SearchResults();
		// todo set as a list the two advanced searchers
		// create those searches
		// use the advanced search parameter
		add(results);
	}

	private void addDocumentSearch(IWContext iwc) {
		// TODO wrap in a block
		add(getLocalizedString("documentsearch.text", "Search for documents on the intranet", iwc));
		addBreak();
		Form form = new Form();
	
		form.addParameter(Searcher.DEFAULT_ADVANCED_SEARCH_PARAMETER_NAME,"true");
		TextInput searchword = new TextInput(DOCUMENT_SEARCH_WORD_PARAMETER_NAME);
		searchword.keepStatusOnAction();
		
		Label label = new Label(getLocalizedString("searchword", "Search:", iwc), searchword);
		DropdownMenu documentType = new DropdownMenu(DOCUMENT_TYPE_PARAMETER_NAME);
		documentType.addMenuElement("*", getLocalizedString("Any type", "Any", iwc) );
		documentType.addMenuElement("*.doc",".doc");
		documentType.addMenuElement("*.pdf",".pdf");
		documentType.addMenuElement("*.xls",".xls");
		documentType.addMenuElement("*.ppt",".ppt");
		
		documentType.keepStatusOnAction();
		
		Label typeLabel = new Label(getLocalizedString("documentType", "Type:", iwc), documentType);
		DropdownMenu sorting = new DropdownMenu(DOCUMENT_ORDERING_PARAMETER_NAME);
		sorting.keepStatusOnAction();
		
		sorting.addMenuElement(DOCUMENT_ORDERING_BY_DATE, getLocalizedString("by date", "date", iwc));
		sorting.addMenuElement(DOCUMENT_ORDERING_BY_NAME, getLocalizedString("by name", "name", iwc));
		sorting.addMenuElement(DOCUMENT_ORDERING_BY_SIZE, getLocalizedString("by size", "size", iwc));
		Label sortingLabel = new Label(getLocalizedString("sorting", "Sort by:", iwc), sorting);
		form.add(label);
		form.add(searchword);
		form.addBreak();
		form.add(typeLabel);
		form.add(documentType);
		form.addBreak();
		form.add(sortingLabel);
		form.add(sorting);
		form.addBreak();
		form.add(new SubmitButton());
		add(form);
	}

	private void addContactSearch(IWContext iwc) {
		// TODO wrap in a block
		add(getLocalizedString("contactsearch.text", "Search for a person within the Swedish church in Malmo", iwc));
		addBreak();
		Form form = new Form();

		form.addParameter(Searcher.DEFAULT_ADVANCED_SEARCH_PARAMETER_NAME,"true");
		
		TextInput searchword = new TextInput(CONTACT_SEARCH_WORD_PARAMETER_NAME);
		searchword.keepStatusOnAction();
		
		Label label = new Label(getLocalizedString("searchword", "Search:", iwc), searchword);
		UserStatusDropdown profession = new UserStatusDropdown(CONTACT_PROFESSION_PARAMETER_NAME);
		profession.keepStatusOnAction();
		// DropdownMenu profession = new
		// DropdownMenu(CONTACT_PROFESSION_PARAMETER_NAME);
		// profession.addMenuElement("Fastighetspersonal");
		// profession.addMenuElement("Musiker");
		// profession.addMenuElement("Diakon/ass");
		Label professionLabel = new Label(getLocalizedString("profession", "Profession:", iwc), profession);
		DropdownMenu workplace = getCongregationMenu(iwc);
		workplace.addMenuElementFirst("novalue","  ");
		workplace.keepStatusOnAction();
		
		Label workplaceLabel = new Label(getLocalizedString("workplace", "Workplace:", iwc), workplace);
		form.add(label);
		form.add(searchword);
		form.addBreak();
		form.add(professionLabel);
		form.add(profession);
		form.addBreak();
		form.add(workplaceLabel);
		form.add(workplace);
		form.addBreak();
		form.add(new SubmitButton());
		add(form);
	}

	protected DropdownMenu getCongregationMenu(IWContext iwc) {
		try {
			SelectorUtility util = new SelectorUtility();
			DropdownMenu menu = (DropdownMenu) getInput(util.getSelectorFromIDOEntities(new DropdownMenu(
					CONTACT_WORKPLACE_PARAMETER_NAME), getBusiness(iwc).getParishes(), "getName"));
			return menu;
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	protected InterfaceObject getInput(InterfaceObject input) {
		if (inputStyleClass != null) {
			input.setStyleClass(inputStyleClass);
		}
		return input;
	}

	protected ApplicationsBusiness getBusiness(IWApplicationContext iwac) {
		try {
			return (ApplicationsBusiness) IBOLookup.getServiceInstance(iwac, ApplicationsBusiness.class);
		}
		catch (IBOLookupException ible) {
			throw new IBORuntimeException(ible);
		}
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	/**
	 * @return Returns the buttonStyleClass.
	 */
	public String getButtonStyleClass() {
		return buttonStyleClass;
	}

	/**
	 * @param buttonStyleClass
	 *            The buttonStyleClass to set.
	 */
	public void setButtonStyleClass(String buttonStyleClass) {
		this.buttonStyleClass = buttonStyleClass;
	}

	/**
	 * @return Returns the headerStyleClass.
	 */
	public String getHeaderStyleClass() {
		return headerStyleClass;
	}

	/**
	 * @param headerStyleClass
	 *            The headerStyleClass to set.
	 */
	public void setHeaderStyleClass(String headerStyleClass) {
		this.headerStyleClass = headerStyleClass;
	}

	/**
	 * @return Returns the inputStyleClass.
	 */
	public String getInputStyleClass() {
		return inputStyleClass;
	}

	/**
	 * @param inputStyleClass
	 *            The inputStyleClass to set.
	 */
	public void setInputStyleClass(String inputStyleClass) {
		this.inputStyleClass = inputStyleClass;
	}

	/**
	 * @return Returns the textStyleClass.
	 */
	public String getTextStyleClass() {
		return textStyleClass;
	}

	/**
	 * @param textStyleClass
	 *            The textStyleClass to set.
	 */
	public void setTextStyleClass(String textStyleClass) {
		this.textStyleClass = textStyleClass;
	}

	protected Text getText(String string) {
		Text text = new Text(string);
		if (textStyleClass != null) {
			text.setStyleClass(textStyleClass);
		}
		return text;
	}

	protected Text getHeader(String string) {
		Text text = new Text(string);
		if (headerStyleClass != null) {
			text.setStyleClass(headerStyleClass);
		}
		return text;
	}

	protected GenericButton getButton(GenericButton button) {
		if (buttonStyleClass != null) {
			button.setStyleClass(buttonStyleClass);
		}
		return button;
	}
}
