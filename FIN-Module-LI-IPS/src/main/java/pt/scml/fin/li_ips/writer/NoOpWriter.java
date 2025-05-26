package pt.scml.fin.li_ips.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;


@Slf4j
public class NoOpWriter implements ItemWriter<Void> {

    @Override
    public void write(Chunk<? extends Void> chunk) throws Exception {
        log.debug("do nothing");
    }
}
