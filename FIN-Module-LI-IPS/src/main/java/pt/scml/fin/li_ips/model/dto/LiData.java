package pt.scml.fin.li_ips.model.dto;

public class LiData{
	private String sAgentCode;
	private String sTransactionType;
	private String sTransactionDate;
	private String sGameNum;
	private String sGameType;
	private Double dTransactionVal;
	private Double dPackageNumber;
	private Integer iTotalPackage;
	
	public LiData (String sAgentCode, String sTransactionType, String sTransactionDate, String sGameNum,
					String sGameType, Double dTransactionVal, Double dPackageNumber, Integer iTotalPackage )throws Exception
	{
		this.sAgentCode = sAgentCode;
		this.sTransactionType = sTransactionType;
		this.sTransactionDate = sTransactionDate;
		this.sGameNum = sGameNum;
		this.sGameType = sGameType;
		this.dTransactionVal = dTransactionVal;
		this.dPackageNumber = dPackageNumber;
		this.iTotalPackage = iTotalPackage;
	}
	
	/*Gets*/
	public String getAgentCode(){ return sAgentCode; }
	public String getTransactionDate(){ return sTransactionDate; }
	public String getTransactionType(){ return sTransactionType; }
	public String getGameNum(){ return sGameNum; }
	public String getGameType(){ return sGameType; }
	public Double getTransactionVal(){ return dTransactionVal; }
	public Double getPackageNumber(){ return dPackageNumber; }
	public Integer getTotalPackage(){ return iTotalPackage; }
	
	/*Sets*/
	public void setAgentCode(String sAgentCode){ this.sAgentCode = sAgentCode; }
    public void setTransactionType(String sTransactionType){ this.sTransactionType = sTransactionType; }
	public void setTransactionDate(String sTransactionDate){ this.sTransactionDate = sTransactionDate; }
	public void setGameNum(String sGameNum){ this.sGameNum = sGameNum; }
	public void setGameType(String sGameType){ this.sGameType = sGameType; }
	public void setTransactionVal(Double dTransactionVal){ this.dTransactionVal = dTransactionVal; }
	public void setPackageNumber(Double dPackageNumber){ this.dPackageNumber = dPackageNumber; }
	public void setTotalPackage(Integer iTotalPackage){ this.iTotalPackage = iTotalPackage; }
	
}