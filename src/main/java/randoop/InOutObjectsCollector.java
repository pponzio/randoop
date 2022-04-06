package randoop;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import randoop.sequence.ExecutableSequence;

public final class InOutObjectsCollector implements ExecutionVisitor {

	private XStream xstream;
	private List<Object> inputs;
	private List<Object> outputs;
	
	public InOutObjectsCollector(XStream xstream) {
		this.xstream = xstream;
	}

	public List<Object> getInputs() {
		return inputs;
	}

	public List<Object> getOutputs() {
		return outputs;
	}

	@Override
	public void initialize(ExecutableSequence executableSequence) {
		inputs = null;
		outputs = null;
	}

	@Override
	public void visitBeforeStatement(ExecutableSequence sequence, int i) {
		// TODO: What happens if there are flakys?
		if (i == sequence.sequence.size() - 1) {
			inputs = new ArrayList<>();
			int last = sequence.sequence.size() - 1;
			for (Object in: sequence.getRuntimeInputs(last))
				inputs.add(cloneObject(in));
		}
	}

	@Override
	public void visitAfterStatement(ExecutableSequence sequence, int i) {
		if (i == sequence.sequence.size() - 1 && sequence.isNormalExecution()) 
			outputs = sequence.getLastStmtValues();
	}

	@Override
	public void visitAfterSequence(ExecutableSequence executableSequence) {
		// do nothing
	}


	private Object cloneObject(Object o) {
		String xml = xstream.toXML(o);
		return xstream.fromXML(xml);
	}
}
