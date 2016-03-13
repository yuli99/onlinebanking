package com.wei.onlinebanking.ui;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

public class GreetingView implements Serializable {
	private static final long serialVersionUID = -8198826027705289054L;

	private int viewLoadCount = 0;

	public void greetOnViewLoad(ComponentSystemEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();

		if (viewLoadCount < 1 && !context.isPostback()) {
			String firstName = (String) event.getComponent().getAttributes()
					.get("firstName");

			FacesMessage message = new FacesMessage(String.format(
					"Account home for user %s", firstName));
			context.addMessage("growlMessages", message);

			viewLoadCount++;
		}
	}

}
