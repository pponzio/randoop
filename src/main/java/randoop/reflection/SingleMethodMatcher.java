package randoop.reflection;

import java.util.regex.Pattern;

import randoop.operation.TypedClassOperation;

public class SingleMethodMatcher extends OmitMethodsPredicate {

	public SingleMethodMatcher(Pattern matcher) {
		super(matcher);
	}
	
	public boolean matches(final TypedClassOperation operation) {
		return shouldOmit(operation);
	}

}
