package se.agura.applications.presentation;

import java.rmi.RemoteException;

import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.presentation.ExceptionWrapper;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.util.EmailValidator;

/*
 * import com.idega.presentation.ExceptionWrapper; import
 * com.idega.presentation.IWContext; import com.idega.presentation.*; import
 * com.idega.presentation.ui.*; import com.idega.core.data.Address; import
 * com.idega.core.data.Email; import com.idega.user.data.*; import
 * com.idega.business.IBOLookup; import com.idega.user.business.UserBusiness;
 * 
 * import se.idega.idegaweb.commune.presentation.CommuneBlock;
 */

/**
 * Title: Description: Copyright: Copyright (c) 2002 Company:
 * 
 * @author Anders Lindman
 * @version 1.0
 */
public class UserAccountPreferences extends ApplicationsBlock {

	private final static int ACTION_VIEW_FORM = 1;
	private final static int ACTION_FORM_SUBMIT = 2;
	private final static int ACTION_CANCEL = 3;

	private final static String PARAMETER_FORM_SUBMIT = "cap_sbmt";
	private final static String PARAMETER_CANCEL = "cap_cncl";
	private final static String PARAMETER_NEW_PASSWORD = "cap_n_pw";
	private final static String PARAMETER_NEW_PASSWORD_REPEATED = "cap_n_pw_r";
	private final static String PARAMETER_EMAIL = "cap_email";
	private final static String PARAMETER_PHONE_WORK = "cap_phn_w";
	private final static String PARAMETER_PHONE_MOBILE = "cap_phn_m";

	private final static String KEY_PREFIX = "citizen.";
	private final static String KEY_NAME = KEY_PREFIX + "name";
	private final static String KEY_PARISH = KEY_PREFIX + "parish";
	private final static String KEY_EMAIL = KEY_PREFIX + "email";
	private final static String KEY_NEW_PASSWORD = KEY_PREFIX + "new_password";
	private final static String KEY_NEW_PASSWORD_REPEATED = KEY_PREFIX + "new_password_repeated";
	private final static String KEY_UPDATE = KEY_PREFIX + "update";
	private final static String KEY_PHONE_MOBILE = KEY_PREFIX + "phone_mobile";
	private final static String KEY_PHONE_WORK = KEY_PREFIX + "phone_work";
	private final static String KEY_EMAIL_INVALID = KEY_PREFIX + "email_invalid";
	private final static String KEY_PREFERENCES_SAVED = KEY_PREFIX + "preferenced_saved";
	private final static String KEY_PASSWORD_EMPTY = KEY_PREFIX + "password_empty";
	private final static String KEY_PASSWORD_REPEATED_EMPTY = KEY_PREFIX + "password_repeated_empty";
	private final static String KEY_PASSWORDS_NOT_SAME = KEY_PREFIX + "passwords_not_same";
	private final static String KEY_PASSWORD_CHAR_ILLEGAL = KEY_PREFIX + "password_char_illegal";	

	private final static String DEFAULT_EMAIL = "E-mail";
	private final static String DEFAULT_NEW_PASSWORD = "New password";
	private final static String DEFAULT_NEW_PASSWORD_REPEATED = "Repeat new password";
	private final static String DEFAULT_UPDATE = "Update";
	private final static String DEFAULT_NAME = "Name";
	private final static String DEFAULT_PARISH = "Parish";
	private final static String DEFAULT_PHONE_WORK = "Phone (work)";
	private final static String DEFAULT_PHONE_MOBILE = "Phone (mobile)";
	private final static String DEFAULT_EMAIL_INVALID = "Email address invalid.";
	private final static String DEFAULT_PREFERENCES_SAVED = "Your preferences has been saved.";
	private final static String DEFAULT_PASSWORD_EMPTY = "Password cannot be empty.";		
	private final static String DEFAULT_PASSWORD_REPEATED_EMPTY = "Repeated password cannot be empty.";		
	private final static String DEFAULT_PASSWORDS_NOT_SAME = "New passwords not the same.";		
	private final static String DEFAULT_PASSWORD_CHAR_ILLEGAL = "Password contains illegal character(s).";		

	public static final String CITIZEN_ACCOUNT_PREFERENCES_PROPERTIES = "citizen_account_preferences";

	private User user = null;
	private Image iButtonImage;

	public UserAccountPreferences() {
	}

	public void present(IWContext iwc) {
		if (!iwc.isLoggedOn()) {
			return;
		}
		this.user = iwc.getCurrentUser();

		try {
			int action = parseAction(iwc);
			switch (action) {
				case ACTION_VIEW_FORM:
					viewPreferencesForm(iwc);
					break;
				case ACTION_FORM_SUBMIT:
					updatePreferences(iwc);
					break;
				case ACTION_CANCEL:
					viewPreferencesForm(iwc);
					break;
			}
		}
		catch (Exception e) {
			super.add(new ExceptionWrapper(e, this));
		}
	}

	private int parseAction(final IWContext iwc) {
		int action = ACTION_VIEW_FORM;

		if (iwc.isParameterSet(PARAMETER_FORM_SUBMIT)) {
			action = ACTION_FORM_SUBMIT;
		}
		else if (iwc.isParameterSet(PARAMETER_CANCEL)) {
			action = ACTION_CANCEL;
		}

		return action;
	}

	private void viewPreferencesForm(IWContext iwc) throws java.rmi.RemoteException {
		drawForm(iwc);
	}

	private void drawForm(IWContext iwc) throws RemoteException {
		Form form = new Form();
		
		Table table = new Table();
		table.setCellpadding(iCellpadding);
		table.setCellspacing(0);
		table.setBorder(1);
		form.add(table);
		int row = 1;

		UserBusiness ub = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);

		String userName = user.getName();
		String parish = "";
		try {
			Group group = getBusiness(iwc).getUserParish(user);
			if (group != null) {
				parish = group.getName();
			}
		}
		catch (RemoteException re) {
			log(re);
		}

		table.add(getHeader(getResourceBundle().getLocalizedString(KEY_NAME, DEFAULT_NAME)), 1, row);
		table.add(getText(userName), 2, row);
		row++;

		table.add(getHeader(getResourceBundle().getLocalizedString(KEY_PARISH, DEFAULT_PARISH)), 1, row);
		table.add(getText(parish), 2, row);
		row++;

		String valueEmail = iwc.getParameter(PARAMETER_EMAIL);
		/*
		 * if the entered address is invalid show the orignal from database if
		 * exists
		 */
		boolean isLegalEmail = false;
		if (valueEmail != null) {
			isLegalEmail = EmailValidator.getInstance().validateEmail(valueEmail);
		}
		
		if (valueEmail == null || !isLegalEmail) {
			Email userMail = ub.getUserMail(user);
			if (userMail != null) {
				valueEmail = userMail.getEmailAddress();
			}
			else {
				valueEmail = "";
			}
		}
		
		String valueNewPassword = iwc.getParameter(PARAMETER_NEW_PASSWORD) != null ? iwc.getParameter(PARAMETER_NEW_PASSWORD) : "";
		String valueNewPasswordRepeated = iwc.getParameter(PARAMETER_NEW_PASSWORD_REPEATED) != null ? iwc.getParameter(PARAMETER_NEW_PASSWORD_REPEATED) : "";
		String valuePhoneMobile = iwc.getParameter(PARAMETER_PHONE_MOBILE);
		if (valuePhoneMobile == null) {
			try {
				Phone p = ub.getUsersMobilePhone(user);
				valuePhoneMobile = p.getNumber();
			}
			catch (NoPhoneFoundException npfe) {
				valuePhoneMobile = "";
			}
		}
		String valuePhoneWork = iwc.getParameter(PARAMETER_PHONE_WORK);
		if (valuePhoneWork == null) {
			try {
				Phone p = ub.getUsersWorkPhone(user);
				valuePhoneWork = p.getNumber();
			}
			catch (NoPhoneFoundException npfe) {
				valuePhoneWork = "";
			}
		}
		Text tEmail = getHeader(getResourceBundle().getLocalizedString(KEY_EMAIL, DEFAULT_EMAIL));
		Text tNewPassword = getHeader(getResourceBundle().getLocalizedString(KEY_NEW_PASSWORD, DEFAULT_NEW_PASSWORD));
		Text tNewPasswordRepeated = getHeader(getResourceBundle().getLocalizedString(KEY_NEW_PASSWORD_REPEATED, DEFAULT_NEW_PASSWORD_REPEATED));
		Text tPhoneWork = getHeader(getResourceBundle().getLocalizedString(KEY_PHONE_WORK, DEFAULT_PHONE_WORK));
		Text tPhoneMobile = getHeader(getResourceBundle().getLocalizedString(KEY_PHONE_MOBILE, DEFAULT_PHONE_MOBILE));

		TextInput tiEmail = (TextInput) getInput(new TextInput(PARAMETER_EMAIL));
		if (valueEmail != null) {
			tiEmail.setValue(valueEmail);
		}
		tiEmail.setAsEmail(getResourceBundle().getLocalizedString(KEY_EMAIL_INVALID, DEFAULT_EMAIL_INVALID));
		TextInput tiPhoneMobile = (TextInput) getInput(new TextInput(PARAMETER_PHONE_MOBILE));
		if (tiPhoneMobile != null) {
			tiPhoneMobile.setValue(valuePhoneMobile);
		}
		TextInput tiPhoneWork = (TextInput) getInput(new TextInput(PARAMETER_PHONE_WORK));
		if (valuePhoneWork != null) {
			tiPhoneWork.setValue(valuePhoneWork);
		}
		PasswordInput tiNewPassword = (PasswordInput) getInput(new PasswordInput(PARAMETER_NEW_PASSWORD));
		if (valueNewPassword != null) {
			tiNewPassword.setValue(valueNewPassword);
		}
		PasswordInput tiNewPasswordRepeated = (PasswordInput) getInput(new PasswordInput(PARAMETER_NEW_PASSWORD_REPEATED));
		if (valueNewPasswordRepeated != null) {
			tiNewPasswordRepeated.setValue(valueNewPasswordRepeated);
		}

		SubmitButton sbUpdate = null;
		if (iButtonImage != null) {
			sbUpdate = new SubmitButton(iButtonImage);
		}
		else {
			sbUpdate = (SubmitButton) getButton(new SubmitButton(getResourceBundle().getLocalizedString(KEY_UPDATE, DEFAULT_UPDATE), PARAMETER_FORM_SUBMIT, "true"));
		}

		table.setHeight(row, 12);

		row++;
		table.add(tNewPassword, 1, row);
		table.add(tiNewPassword, 2, row);

		row++;
		table.add(tNewPasswordRepeated, 1, row);
		table.add(tiNewPasswordRepeated, 2, row);

		row++;
		table.setHeight(row, 12);

		row++;
		table.add(tEmail, 1, row);
		table.add(tiEmail, 2, row);

		row++;
		table.add(tPhoneMobile, 1, row);
		table.add(tiPhoneMobile, 2, row);

		row++;
		table.add(tPhoneWork, 1, row);
		table.add(tiPhoneWork, 2, row);

		row++;
		table.setHeight(row, 12);

		row++;
		table.add(sbUpdate, 1, row);
		
		table.setWidth(1, iHeaderColumnWidth);
		table.setCellpaddingLeft(1, 0);

		add(form);
	}

	private void updatePreferences(IWContext iwc) throws Exception {
		LoginTable loginTable = LoginDBHandler.getUserLogin(((Integer) user.getPrimaryKey()).intValue());
		String login = loginTable.getUserLogin();
		String newPassword1 = iwc.getParameter(PARAMETER_NEW_PASSWORD);
		String newPassword2 = iwc.getParameter(PARAMETER_NEW_PASSWORD_REPEATED);
		String sEmail = iwc.getParameter(PARAMETER_EMAIL);
		String phoneMobile = iwc.getParameter(PARAMETER_PHONE_MOBILE);
		String phoneWork = iwc.getParameter(PARAMETER_PHONE_WORK);

		String errorMessage = null;
		boolean updatePassword = false;
		boolean updateEmail = false;

		try {

			// Validate new password
			if (!newPassword1.equals("") || !newPassword2.equals("")) {
				if (newPassword1.equals("")) {
					throw new Exception(getResourceBundle().getLocalizedString(KEY_PASSWORD_EMPTY, DEFAULT_PASSWORD_EMPTY));
				}
				if (newPassword2.equals("")) {
					throw new Exception(getResourceBundle().getLocalizedString(KEY_PASSWORD_REPEATED_EMPTY, DEFAULT_PASSWORD_REPEATED_EMPTY));
				}
				if (!newPassword1.equals(newPassword2)) {
					throw new Exception(getResourceBundle().getLocalizedString(KEY_PASSWORDS_NOT_SAME, DEFAULT_PASSWORDS_NOT_SAME));
				}
				for (int i = 0; i < newPassword1.length(); i++) {
					char c = newPassword1.charAt(i);
					boolean isPasswordCharOK = false;
					if ((c >= 'a') && (c <= 'z')) {
						isPasswordCharOK = true;
					}
					else if ((c >= 'A') && (c <= 'Z')) {
						isPasswordCharOK = true;
					}
					else if ((c >= '0') && (c <= '9')) {
						isPasswordCharOK = true;
					}
					else if ((c == 'Œ') || (c == 'Š') || (c == 'š')) {
						isPasswordCharOK = true;
					}
					else if ((c == '') || (c == '€') || (c == '…')) {
						isPasswordCharOK = true;
					}
					if (!isPasswordCharOK) {
						throw new Exception(getResourceBundle().getLocalizedString(KEY_PASSWORD_CHAR_ILLEGAL, DEFAULT_PASSWORD_CHAR_ILLEGAL));
					}
				}
				updatePassword = true;
			}

			updateEmail = EmailValidator.getInstance().validateEmail(sEmail);

		}
		catch (Exception e) {
			errorMessage = e.getMessage();
		}

		if (errorMessage != null) {
			add(getErrorText(" " + errorMessage));
		}
		else {
			// Ok to update preferences
			UserBusiness ub = (UserBusiness) IBOLookup.getServiceInstance(iwc, UserBusiness.class);

			if (updatePassword) {
				LoginDBHandler.updateLogin(((Integer) user.getPrimaryKey()).intValue(), login, newPassword1);
			}
			if (updateEmail) {
				ub.storeUserEmail(user, sEmail, true);
			}
			ub.updateUserWorkPhone(user, phoneWork);
			ub.updateUserMobilePhone(user, phoneMobile);
		}
		drawForm(iwc);
		if (errorMessage == null) {
			add(new Break());
			add(getHeader(getResourceBundle().getLocalizedString(KEY_PREFERENCES_SAVED, DEFAULT_PREFERENCES_SAVED)));
		}
	}
	
	public void setButtonImage(Image buttonImage) {
		iButtonImage = buttonImage;
	}
}