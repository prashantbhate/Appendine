package org.bhate.Appendine.client;

import java.util.List;

import org.bhate.Appendine.shared.FieldVerifier;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Appendine implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		VerticalPanel baseVPanel = new VerticalPanel();
		baseVPanel.addStyleName("dialogVPanel");
		baseVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);

		final TextBox nameField = new TextBox();
		nameField.addStyleName("nameField");
		nameField.setText("");
		nameField.setMaxLength(12);
		nameField.setFocus(true);
		nameField.selectAll();
		VerticalPanel vp1 = new VerticalPanel();
		vp1.addStyleName("ds");
		vp1.add(nameField);
		baseVPanel.add(vp1);

		final Button sendButton = new Button("Ready");
		sendButton.addStyleName("sendButton");
		VerticalPanel vp2 = new VerticalPanel();
		vp2.addStyleName("ds");
		vp2.add(sendButton);
		baseVPanel.add(vp2);

		Label spacer = new Label();
		spacer.addStyleName("spacer");
		baseVPanel.add(spacer);

		final Label errorLabel = new Label();
		errorLabel.addStyleName("serverResponseLabelError");
		baseVPanel.add(errorLabel);
		spacer = new Label();
		spacer.addStyleName("spacer");
		baseVPanel.add(spacer);

		final FlexTable cloud = new FlexTable();
		cloud.setCellPadding(2);
		cloud.setCellSpacing(2);
		cloud.addStyleName("cloud");
		baseVPanel.add(cloud);
		spacer = new Label();
		spacer.addStyleName("spacer");
		baseVPanel.add(spacer);

		final Label inputsLabel = new Label();
		inputsLabel.addStyleName("inputsLabel");
		baseVPanel.add(inputsLabel);
		final FlexTable inputs = new FlexTable();
		inputs.setCellPadding(2);
		inputs.setCellSpacing(2);
		inputs.addStyleName("inputs");
		baseVPanel.add(inputs);

		RootPanel.get("resultContainer").add(baseVPanel);

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler,
				ValueChangeHandler<String> {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				History.newItem(nameField.getText());
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_ENTER:
					if (event.isControlKeyDown() && event.isAltKeyDown()
							&& event.isShiftKeyDown())
						sendPrefixToServer();
					else
						History.newItem(nameField.getText());
					break;
				}
			}

			private void update(FlexTable t, List<String> words) {
				int L = (int) Math.sqrt(words.size());
				if (L >= 9)
					L = 9;
				int i = 0;
				t.clear();
				for (String word : words) {
					int c = i % L;
					int r = i / L;
					t.setWidget(r, c, new HTML(word));
					i++;
				}
			}

			private void sendPrefixToServer() {
				errorLabel.setText("");
				inputsLabel.setText("");
				cloud.clear();
				inputs.clear();

				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidSuffix(textToServer)) {
					errorLabel.setText("not a valid suffix");
					return;
				}
				nameField.setEnabled(false);
				greetingService.storeSuffix(nameField.getText(),
						new AsyncCallback<Void>() {
							public void onFailure(Throwable caught) {
								errorLabel.setText(SERVER_ERROR + "\n"
										+ caught.getMessage());
								nameField.setEnabled(true);
							}

							public void onSuccess(Void v) {
								inputsLabel
										.setText("added suffix to the endine...");
								nameField.setEnabled(true);
							}
						});
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 * @param textToServer 
			 */
			private void sendNameToServer(String textToServer) {
				// First, we validate the input.
				errorLabel.setText("");
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel
							.setText("need a valid name with least four characters in it [ Hint: name is (a-zA-Z) but not too big too ]");
					return;
				}
				nameField.setEnabled(false);
				greetingService.greetServer(textToServer,
						new AsyncCallback<List<String>>() {
							public void onFailure(Throwable caught) {
								errorLabel.setText(SERVER_ERROR + "\n"
										+ caught.getMessage());
								nameField.setEnabled(true);
							}

							public void onSuccess(List<String> words) {
								update(cloud, words);
								showInputs();
								nameField.setEnabled(true);
							}

						});
			}

			private void showInputs() {
				greetingService.getInputs(new AsyncCallback<List<String>>() {
					public void onFailure(Throwable caught) {
						errorLabel.setText(SERVER_ERROR + "\n"
								+ caught.getMessage());
					}

					public void onSuccess(List<String> words) {
						inputsLabel.setText("some recently appended words...");
						update(inputs, words);
					}

				});

			}

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String value = event.getValue();
				if(value==null || value.equals(""))
					return;
				nameField.setText(value);
				sendNameToServer(value);
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);

		History.addValueChangeHandler(handler);
		History.fireCurrentHistoryState();
	}

}
