
package gov.epa.exp_data_gathering.parse.QSAR_ToolBox2;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;

public interface QSARRecordMapper {
    ExperimentalRecord map(RecordQSAR_ToolBox2 row);
}
