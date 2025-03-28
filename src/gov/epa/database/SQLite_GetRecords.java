package gov.epa.database;

import java.awt.List;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class SQLite_GetRecords {
	/**
	 * Oddly search for multiple CAS numbers in one query is slower than multiple single chemical queries
	 * 
	 * @param tableName
	 * @param keyField
	 * @param vec
	 */
	public static void getRecords(String databasePath,String tableName,String keyField,Vector<String> vec) {
		try {
			Statement stat=SQLite_Utilities.getStatement(databasePath);
			getRecords(stat, tableName, keyField, vec);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static ResultSet getRecords(String databasePath,String tableName,String keyField,Object keyValue) {
		Statement stat = SQLite_Utilities.getStatement(databasePath);
		return getRecords(stat, tableName, keyField, keyValue);
	}
	
	/**
	 * Search for multiple values in single query
	 * <br>
	 * Oddly search for multiple CAS numbers in one query is slower than multiple single chemical queries
	 * 
	 * @param tableName
	 * @param keyField
	 * @param vec
	 */
	public static ResultSet getRecords(Statement stat,String tableName, String keyField,Vector<String> vec) {

		try {

			String query="select * from "+tableName+" where "+keyField+" = ";

			for (int i=0;i<vec.size();i++) {
				query+="\""+vec.get(i)+"\"";

				if (i<vec.size()-1) {
					query+=" or "+keyField+" = " ;
				} else {
					query+=";";
				}
			}

			//			System.out.println(query);
			ResultSet rs = stat.executeQuery(query);
			//			printResultSet(rs);
			return rs;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	

	public static ResultSet getRecords(Statement stat,String sql) {

		try {

			//			System.out.println(query);
			ResultSet rs = stat.executeQuery(sql);
			//			ResultSetMetaData rsmd = rs.getMetaData();

			return rs;
			//			this.printResultSet(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}	
	
	
	public static ResultSet getAllRecords(Statement stat,String tableName) {

		try {
			String query="select * from "+tableName+";";
			//			System.out.println(query);
			ResultSet rs = stat.executeQuery(query);
			//			ResultSetMetaData rsmd = rs.getMetaData();

			return rs;
			//			this.printResultSet(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}	


	public static ResultSet getRecords(Statement stat,String tableName,String keyField,Object keyValue) {

		try {
			String query="select * from "+tableName+" where "+keyField+" = \""+keyValue+"\";";
			//			System.out.println(query);
			ResultSet rs = stat.executeQuery(query);
			//			ResultSetMetaData rsmd = rs.getMetaData();

			return rs;
			//			this.printResultSet(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static void createRecord(ResultSet rs, Object r) {
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();

			int columnCount = rsmd.getColumnCount();

			// The column count starts from 1
			for (int i = 1; i <= columnCount; i++ ) {
				String name = rsmd.getColumnLabel(i);

				if(rs.getObject(i)==null)continue;
				
				String val=rs.getString(i);
				
				if(val.equals("-") || val.isBlank()) continue;

				//				System.out.println(name+"\t"+val);

				if (val!=null) {
					
					if(r.getClass().getDeclaredField(name)==null) {
						continue;
					}
					
					Field myField = r.getClass().getDeclaredField(name);	
					
					String type=myField.getType().getName();
					
					if (type.contentEquals("boolean")) {
						myField.setBoolean(r, Boolean.parseBoolean(val));
					} else if (type.contentEquals("double")) {
						myField.setDouble(r, Double.parseDouble(val));
					} else if (type.contentEquals("int")) {
						myField.setInt(r, Integer.parseInt(val));

					} else if (type.contentEquals("java.lang.Double")) {
						
//						System.out.println(name+"\tDouble");
						try {
							Double dval=Double.parseDouble(val);						
							myField.set(r, dval);
						} catch (Exception ex) {
							System.out.println("Error parsing "+val+" for field "+name+" to Double for "+rs.getString(1));
						}
					} else if (type.contentEquals("java.lang.Integer")) {
						Integer ival=Integer.parseInt(val);
						myField.setInt(r,ival);
					} else if (type.contentEquals("java.lang.String")) {
						myField.set(r, val);
					} else if (type.contentEquals("java.util.Set")) {
//						System.out.println(name+"\t"+val);
						val=val.replace("[", "").replace("]", "");
						
						String  [] values = val.split(", ");
						Set<String>list=new HashSet<>();
						for (String value:values) {
							list.add(value.trim());
						}
						myField.set(r,list);

					} else if (type.contentEquals("java.util.List")) {
//						System.out.println(name+"\t"+val);
						val=val.replace("[", "").replace("]", "");
						
						String  [] values = val.split(",");
						ArrayList<String>list=new ArrayList<>();
						for (String value:values) {
							list.add(value.trim());
						}
						myField.set(r,list);
					
					} else {
						System.out.println("Need to implement: "+myField.getType().getName());
					}					
										
				}

			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
