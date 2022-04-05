package randoop.generation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.XStream;

import randoop.InOutObjectsCollector;
import randoop.operation.TypedClassOperation;
import randoop.operation.TypedOperation;
import randoop.reflection.SingleMethodMatcher;
import randoop.sequence.ExecutableSequence;

public class InOutMethodSerializer implements IEventListener {

	private Pattern methodPattern;
	private SingleMethodMatcher methodMatcher;
	private TypedClassOperation operation;
	private String outputFolder;
	private XStream xstream;
	private List<ObjectOutputStream> inOoss;
	private List<ObjectOutputStream> outOoss;
	private InOutObjectsCollector inOutCollector;
	private int inObjs = -1;
	private int outObjs = -1;
	private boolean first = true;

	public InOutMethodSerializer(XStream xstream, Pattern method, String outputFolder, InOutObjectsCollector inOutCollector) {
		this.xstream = xstream;
		this.methodPattern = method;
		this.methodMatcher = new SingleMethodMatcher(methodPattern);
		this.outputFolder = outputFolder;
		this.inOutCollector = inOutCollector;
	}
	
	@Override
	public void explorationStart() { 

	}

	@Override
	public void explorationEnd() {
		closeStream(inOoss);
		closeStream(outOoss);
	}

	@Override
	public void generationStepPre() { 
		inOoss = new ArrayList<>();
		outOoss = new ArrayList<>();
	}

	@Override
	public void generationStepPost(ExecutableSequence s) {
		TypedOperation lastOp = s.sequence.getStatement(s.sequence.size() - 1).getOperation();
		if (!(lastOp instanceof TypedClassOperation)) 
			return;
		TypedClassOperation typedLastOp = (TypedClassOperation) lastOp;
		if (!methodMatcher.matches(typedLastOp)) 
			return;

		
		List<Object> inputs = inOutCollector.getInputs();
		List<Object> outputs = inOutCollector.getOutputs();
		if (first) {
			operation = typedLastOp;
			inObjs = inputs.size();
			outObjs = outputs.size();
			createStream(inObjs, inOoss, "in");
			createStream(outObjs, outOoss, "out");
			first = false;
		}
		else 
			consistencyChecks(s, typedLastOp, inputs, outputs);

		writeObjects(inputs, inOoss);
		writeObjects(outputs, outOoss);
	}

	private void consistencyChecks(ExecutableSequence s, TypedClassOperation typedLastOp, List<Object> inputs,
			List<Object> outputs) {
		// Consistency checks
		assert operation.equals(typedLastOp) : 
			String.format("Serializing inputs/outputs for two different methods not allowed. "
					+ "\nMatching method 1: %s"
					+ "\nMatching method 2: %s", 
					operation.toParsableString(), 
					typedLastOp.toParsableString());
		assert inObjs == inputs.size() : 
			String.format("Serializing %d inputs but current operation has %d inputs."
					+ "\nSequence: ", 
					inObjs, 
					inputs.size(), 
					s.toCodeString());
		assert outObjs == outputs.size() : 
			String.format("Serializing %d outputs but current operation has %d outputs."
					+ "\nSequence: ", 
					outObjs, 
					outputs.size(), 
					s.toCodeString());
	}

	@Override
	public void progressThreadUpdate() { }

	@Override
	public boolean shouldStopGeneration() {
		return false;
	}
	
	
	private void createStream(int n, List<ObjectOutputStream> loos, String inOut) {
		for (int k = 0; k < n; k++) {
			String currFile = outputFolder + "/" + inOut + String.valueOf(k) + ".xml";
			try {
				loos.add(xstream.createObjectOutputStream(
						   new FileOutputStream(currFile)));
			} catch (IOException e) {
				throw new Error("Cannot create serial file: " + currFile);
			}
		}
	}
	
	private void writeObjects(List<Object> objs, List<ObjectOutputStream> loos) {
		for (int k = 0; k < objs.size(); k++) {
			try {
				loos.get(k).writeObject(objs.get(k));	
			} catch (IOException e) {
				throw new Error("Cannot serialize object: " + objs.get(k).toString());
			}
		}
	}
	
	private void closeStream(List<ObjectOutputStream> loos) {
		for (ObjectOutputStream oos: loos) {
			try {
				oos.close();
			} catch (IOException e) {
				throw new Error("Cannot close files in folder: " + outputFolder);
			}
		}
	}
	

}
