package pt.scml.fin.li_ips.model.dto;

import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LiDetailsValid {
	

	String sTransactionValidType;
	String sTransactionValidDate;
	String sAgentCode;
	String sAgentCodePriv;
	String sIdentifierEnv;
	String sCreditFlag;
	String sGameNum;
	int    iPackageNum;
	String sVrim;
	String sPin;
	String sTotalTransactValidNum;
	String sTotalTransactValidDec;
	double dTotalTransactionValid;
	String sGameType;
	String sPackageState;
	String sMerchanFlag;
	
	public LiDetailsValid() throws Exception{}
	

	public LiDetailsValid(String fileBuffer) throws Exception{
		
		try{
			
			sTransactionValidType 	= fileBuffer.substring(0,2);
			sTransactionValidDate 	= fileBuffer.substring(2,8);
			sAgentCode 				= fileBuffer.substring(8,15);
			sAgentCodePriv 			= fileBuffer.substring(15,22);
			sIdentifierEnv			= fileBuffer.substring(22,31);
			sCreditFlag				= fileBuffer.substring(31,32);
			sGameNum				= fileBuffer.substring(32,35);
			iPackageNum				= Integer.parseInt(fileBuffer.substring(35,42),10);
			sVrim					= fileBuffer.substring(42,51);
			sPin					= fileBuffer.substring(51,55);
			sTotalTransactValidNum 	= fileBuffer.substring(55,62);
			sTotalTransactValidDec 	= fileBuffer.substring(62,64);
			dTotalTransactionValid 	= Double.parseDouble(sTotalTransactValidNum + "."+ sTotalTransactValidDec);
			sGameType				= fileBuffer.substring(64,65);
			sPackageState			= fileBuffer.substring(65,66);
			sMerchanFlag			= fileBuffer.substring(66,67);
			
		}catch (Exception ex) 
		{
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			log.error("( ERRO ) [ LiDetailsValid () ] ao criar uma nova instancia de LiDetailsValid: "+ sw.toString());
			sw = null;
			throw new Exception ("Erro ao criar uma nova instancia de LiDetailsValid");
		}
		
	}
	
	/*Gets*/
	public String getTransactionValidType(){ return sTransactionValidType; }
	public String getTransactionValidDate(){ return sTransactionValidDate; }
	public String getAgentCode(){ return sAgentCode; }
	public String getAgentCodePriv(){ return sAgentCodePriv; }
	public String getIdentifierEnv(){ return sIdentifierEnv; }
	public String getCreditFlag(){ return sCreditFlag; }
	public String getGameNum(){ return sGameNum; }
	public Integer getPackageNum(){ return iPackageNum; }
	public String getVrim(){ return sVrim; }
	public String getPin(){ return sPin; }
	public Double getTotalTransactionValid(){ return dTotalTransactionValid; }
	public String getGameType(){ return sGameType; }
	public String getPackageState(){ return sPackageState; }
	public String getMerchanFlag(){ return sMerchanFlag; }
	
}