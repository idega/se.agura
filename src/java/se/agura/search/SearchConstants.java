/*
 * $Id: SearchConstants.java,v 1.3 2005/06/22 18:05:19 eiki Exp $
 * Created on Mar 18, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.agura.search;


public interface SearchConstants {
	
	
	public static final String CONTACT_SEARCH = "contact_s";
	public static final String CONTACT_SEARCH_WORD_PARAMETER_NAME = "contact_s_word";
	public static final String CONTACT_PROFESSION_PARAMETER_NAME = "contact_prof";
	public static final String CONTACT_WORKPLACE_PARAMETER_NAME = "contact_work";
	
	//TODO add dependency : didn't have time to add dependecy to ContentSearch
	public static final String DOCUMENT_SEARCH = "doc_s";
	public static final String DOCUMENT_SEARCH_WORD_PARAMETER_NAME = "doc_s_word";
	public static final String DOCUMENT_TYPE_PARAMETER_NAME = "doc_type";
	public static final String DOCUMENT_ORDERING_PARAMETER_NAME = "doc_order";
	public static final String DOCUMENT_ORDERING_BY_DATE = "getlastmodified";
	public static final String DOCUMENT_ORDERING_BY_NAME = "displayname";
	public static final String DOCUMENT_ORDERING_BY_SIZE = "getcontentlength";
	
}
