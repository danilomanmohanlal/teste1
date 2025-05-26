package pt.scml.fin.li_ips.model.dto;

import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LiDetails {


    String agent_Code;
    String transaction_type;
    String tr_dt;
    String tr_t;
    String game_num;
    String game_type;
    String transaction_num;
    String transaction_dec;
    double transaction_val;
    double package_number;
    int total_package;
    String transaction_date;

    public LiDetails() throws Exception {
    }

    /**
     * @param fileBuffer
     * @throws Exception
     */
    public LiDetails(String fileBuffer) throws Exception {

        try {

            agent_Code = fileBuffer.substring(0, 7);
            transaction_type = fileBuffer.substring(7, 9);
            tr_dt = fileBuffer.substring(9, 17);
            tr_t = fileBuffer.substring(17, 23);
            game_num = fileBuffer.substring(25, 28);
            game_type = fileBuffer.substring(28, 30);
            transaction_num = fileBuffer.substring(30, 40);
            transaction_dec = fileBuffer.substring(40, 42);
            package_number = Double.parseDouble(fileBuffer.substring(42, 49));
            total_package = Integer.parseInt(fileBuffer.substring(49, 52), 10);
            transaction_date = tr_dt + tr_t;
            transaction_val = Double.parseDouble(transaction_num + "." + transaction_dec);


        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            log.error("( ERRO ) [ LiDetails () ] ao criar uma nova instancia de LiDetails: "
                    + sw.toString());
            sw = null;
            throw new Exception("Erro ao criar uma nova instancia de LiDetails");
        }

    }

    /*Gets*/
    public String getAgentCode() {
        return agent_Code;
    }

    public String getTransactionType() {
        return transaction_type;
    }

    public String getGameNum() {
        return game_num;
    }

    public String getGameType() {
        return game_type;
    }

    public Double getTransactionVal() {
        return transaction_val;
    }

    public String getTransactionDate() {
        return transaction_date;
    }

    public Double getPackageNumber() {
        return package_number;
    }

    public Integer getTotalPackage() {
        return total_package;
    }

    /*Sets*/
    public void setAgentCode(String agent_code) {
        this.agent_Code = agent_code;
    }

    public void setTransactionType(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public void setGameNum(String game_num) {
        this.game_num = game_num;
    }

    public void setGameType(String game_type) {
        this.game_type = game_type;
    }

    public void setTransactionVal(double transaction_val) {
        this.transaction_val = transaction_val;
    }

    public void SetTransactionDate(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public void SetPackageNumber(Double package_number) {
        this.package_number = package_number;
    }

    public void SetTotalPackage(Integer total_package) {
        this.total_package = total_package;
    }

}
