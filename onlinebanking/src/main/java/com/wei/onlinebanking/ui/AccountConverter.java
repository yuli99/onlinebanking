package com.wei.onlinebanking.ui;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.wei.onlinebanking.domain.BankAccount;
import com.wei.onlinebanking.services.AccountService;

public class AccountConverter implements Converter {

	private AccountService accountService;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {

		if (value == null || value.isEmpty()) {
			return null;
		}

		try {
			return accountService.loadAccountById(Long.parseLong(value));
		} catch (NumberFormatException exc) {
			FacesMessage message = constructErrorMessage(
					null,
					String.format(getMessageBundle().getString(
							"acctConverterErrorMsg")));
			throw new ConverterException(message, exc);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object object) {
		if (object != null) {
			return String.valueOf(((BankAccount) object).getId());
		} else {
			return "";
		}
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	// message constructor
	protected FacesMessage constructErrorMessage(String message, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, message, detail);
	}

	// wrapper static method call
	protected ResourceBundle getMessageBundle() {
		return ResourceBundle.getBundle("messages");
	}

}
