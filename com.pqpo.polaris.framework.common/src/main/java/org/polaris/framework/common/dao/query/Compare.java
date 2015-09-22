package org.polaris.framework.common.dao.query;

/**
 * 比较的类型
 * 
 * @author wang.sheng
 * 
 */
public enum Compare
{
	Eq("=?"),
	NoEq("<>?"),
	Gt(">?"), 
	Lt("<?"), 
	GtEq(">=?"), 
	LtEq("<=?"),
	Like(" like ?"),
	NoLike(" not like ?");
	
	private String operators = "";
	private Compare(String operators) {
		this.operators = operators;
	}
	public String getOperators() {
		return operators;
	}
}
