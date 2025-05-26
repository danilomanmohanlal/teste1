package pt.scml.fin.li_ips.model.dto;

public class LiDetailsInvoice {

	private String sStationCode;
	private String sGameId;
	private String sContestName;
	private Integer iSalesQT;
	private Double dSalesAmount;
	private Integer iCancelQT;
	private Double dCancelAmount;
	private Integer iPrizeQT;
	private Double dPrizeAmount;

	public LiDetailsInvoice (String sStationCode, String sGameId, String sContestName, 
			Integer iSalesQT, Double dSalesAmount, Integer iCancelQT, Double dCancelAmount,
			Integer iPrizeQT, Double dPrizeAmount)throws Exception
	{
		this.sStationCode = sStationCode;
		this.sGameId = sGameId;
		this.sContestName = sContestName;
		this.iSalesQT = iSalesQT;
		this.dSalesAmount = dSalesAmount;
		this.iCancelQT = iCancelQT;
		this.dCancelAmount = dCancelAmount;
		this.iPrizeQT = iPrizeQT;
		this.dPrizeAmount = dPrizeAmount;
	}
	
	/*Gets*/
	public String getStationCode(){ return sStationCode; }
	public String getGameId(){ return sGameId; }
	public String getContestName(){ return sContestName; }
	public Integer getSalesQT(){ return iSalesQT; }
	public Double getSalesAmount(){ return dSalesAmount; }
	public Integer getCancelQT(){ return iCancelQT; }
	public Double getCancelAmount(){ return dCancelAmount; }	
	public Integer getPrizeQT(){ return iPrizeQT; }
	public Double getPrizeAmount(){ return dPrizeAmount; }	
	
	/*Sets*/
	public void setStationCode(String sStationCode){ this.sStationCode = sStationCode; }
	public void setGameId(String sGameId){ this.sGameId = sGameId; }	
	public void setContestName(String sContestName){ this.sContestName = sContestName; }	
	public void setSalesQT(Integer iSalesQT){ this.iSalesQT = iSalesQT; }	
	public void setSalesAmount(Double dSalesAmount){ this.dSalesAmount = dSalesAmount; }	
	public void setCancelQT(Integer iCancelQT){ this.iCancelQT = iCancelQT; }	
	public void setCancelAmount(Double dCancelAmount){ this.dCancelAmount = dCancelAmount; }	
	public void setPrizeQT(Integer iPrizeQT){ this.iPrizeQT = iPrizeQT; }	
	public void setPrizeAmount(Double dPrizeAmount){ this.dPrizeAmount = dPrizeAmount; }
	
}