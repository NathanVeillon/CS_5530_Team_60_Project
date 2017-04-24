package main.java.models;

import main.java.models.base.BaseObjectQuery;

import java.util.HashSet;

/**
 * Created by StudentNathan on 3/25/2017.
 */
public class TemporaryHousingQuery extends BaseObjectQuery<TemporaryHousing> {
	public TemporaryHousingQuery() {
		super(TemporaryHousing.class);
	}


	private enum SpecialCase {AverageFeedbackScore, AverageTrustedFeedbackScore}
	private HashSet<SpecialCase> CurrentSpecialCases = new HashSet<>();
	private int CurrentUserId;

	public BaseObjectQuery setCurrentUserId(int currentUserId){
		CurrentUserId = currentUserId;
		return this;
	}

	public String getAliasedDatabaseFeild(String fieldName) throws Exception{
		if(containsSpecialCase(fieldName)){
			CurrentSpecialCases.add(SpecialCase.valueOf(fieldName));
			return fieldName;
		}

		return super.getAliasedDatabaseFeild(fieldName);
	}

	protected String getAliasedColumnsToSelect() throws Exception{
		String aliasedColumnsToSelect = super.getAliasedColumnsToSelect();


		StringBuilder specialCaseColumns = new StringBuilder();
		for (SpecialCase specialCase: CurrentSpecialCases) {
			switch (specialCase){
				case AverageFeedbackScore:
					specialCaseColumns.append(", ( SELECT AVG(score) FROM ");
					specialCaseColumns.append(Feedback.TableName);
					specialCaseColumns.append(" `t1` WHERE `t1`.`idTH` = `");
					specialCaseColumns.append(TemporaryHousing.TableName);
					specialCaseColumns.append("1`.`idTH`");
					specialCaseColumns.append(") AS `AverageFeedbackScore`");
					break;
				case AverageTrustedFeedbackScore:
					specialCaseColumns.append(", ( SELECT AVG(score) FROM ");
					specialCaseColumns.append(Feedback.TableName);
					specialCaseColumns.append(" `t1` JOIN ");
					specialCaseColumns.append(UserTrust.TableName);
					specialCaseColumns.append(" `t2` ON (`t1`.`idUser` = `t2`.`idUser2`) WHERE `t1`.`idTH` = `");
					specialCaseColumns.append(TemporaryHousing.TableName);
					specialCaseColumns.append("1`.`idTH` AND `t2`.`idUser1` = ");
					specialCaseColumns.append(CurrentUserId);
					specialCaseColumns.append(" AND `t2`.`isTrusted` = 1");
					specialCaseColumns.append(") AS `AverageTrustedFeedbackScore`");
					break;
			}
		}

		return aliasedColumnsToSelect+specialCaseColumns.toString();
	}

	private static boolean containsSpecialCase(String fieldName) {

		for (SpecialCase c : SpecialCase.values()) {
			if (c.name().equals(fieldName)) {
				return true;
			}
		}

		return false;
	}
}
