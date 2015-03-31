package nl.utwente.group10.ui.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class represents a connection between two different FunctionBlocks. The
 * output of one FunctionBlock will be used as input for another FunctionBlock
 */
public class Connection extends Line implements ChangeListener<Number> , Initializable {

	/** The Block that inputs data into this connection */
	private Block input;
	/** The Block that we output data into from this connection */
	private Block output;
	/** The argument field of the FunctionBlock that we are outputting data into */
	private int outputarg;
	/** The fxmlLoader responsible for loading the fxml.*/
	private FXMLLoader fxmlLoader;

	/**
	 * Method that creates a new instance of this class along with it's visual
	 * representation. 
	 * @param from Anchor to start from.
	 * @param toAnchor Anchor to end at.
	 * @return a new instance of this class
	 * @throws IOException
	 */
	public Connection(OutputAnchor from, InputAnchor to) throws IOException {
		fxmlLoader = new FXMLLoader(getClass().getResource("/ui/Connection.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		input = from.getBlock();
		output = to.getBlock();

		input.layoutXProperty().addListener(this);
		input.layoutYProperty().addListener(this);

		output.layoutXProperty().addListener(this);
		output.layoutYProperty().addListener(this);

		this.setStartAnchor(from);
		this.setEndAnchor(to);
		
		fxmlLoader.load();

		updateStartEndPositions();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	/**
	 * Private method that sets the input into this Connection
	 * @param input source
	 */
	private void setInput(FunctionBlock in) {
		input = in;
	}

	/**
	 * Private method that sets the FunctionBlock and argument 
	 * that Connection will output into.
	 * @param out FunctionBlock to output into
	 * @param outarg Argument field to output into
	 */
	private void setOutput(FunctionBlock out, int outarg) {
		output = out;
		outputarg = outarg;
	}
	
	/**
	 * @return Block that is being used as input
	 */
	public Block getInputFunction() {
		return input;
	}
	
	/**
	 * @return Block that is being used as output
	 */
	public Block getOutputFunction() {
		return output;
	}

	@Override
	public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue) {
		updateStartEndPositions();
	}
}