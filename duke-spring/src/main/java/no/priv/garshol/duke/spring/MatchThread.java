package no.priv.garshol.duke.spring;

import no.priv.garshol.duke.ConfigurationImpl;
import no.priv.garshol.duke.Processor;
import no.priv.garshol.duke.Record;
import no.priv.garshol.duke.matchers.MatchListener;

import java.util.ArrayList;
import java.util.Collection;

public class MatchThread extends Thread {

    protected Collection<Record> records;
    protected boolean matchall;
    private Processor threadProcessor;

    public MatchThread() {

    }

    public MatchThread(int threadno, int recordcount, boolean matchall) {
        super("MatchThread " + threadno);
        this.records = new ArrayList(recordcount);
        this.matchall = matchall;
    }

    public void init(Processor processor,Collection<Record> records){
        this.records = records;
        threadProcessor=new Processor(new ConfigurationImpl());
        for (MatchListener matchListener : processor.getListeners()) {
            threadProcessor.addMatchListener(matchListener);
        }
    }

    @Override
    public void run() {
        threadProcessor.deduplicate(records);
    }

    public void addRecord(Record record) {
        records.add(record);
    }
}