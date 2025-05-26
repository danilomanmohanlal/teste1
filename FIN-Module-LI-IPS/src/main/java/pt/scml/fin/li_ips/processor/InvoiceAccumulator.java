package pt.scml.fin.li_ips.processor;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;

@Component
public class InvoiceAccumulator {

    private final ConcurrentMap<String, FinDailyInvoiceDTO> invoiceMap = new ConcurrentHashMap<>();

    public void accumulate(FinDailyInvoiceDTO dto) {
        String key = generateKey(dto);

        invoiceMap.merge(key, dto, (existing, incoming) -> {
            // merge logic: accumulate salesQt and salesAmount
            existing.setSalesQt(existing.getSalesQt() + incoming.getSalesQt());
            existing.setSalesAmount(existing.getSalesAmount().add(incoming.getSalesAmount()));
            return existing;
        });
    }

    public Collection<FinDailyInvoiceDTO> getAll() {
        return invoiceMap.values();
    }

    private String generateKey(FinDailyInvoiceDTO dto) {
        return dto.getStationCode() + "|" + dto.getContestName(); //contest name is being used for game number
    }
}