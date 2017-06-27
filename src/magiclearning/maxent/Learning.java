package magiclearning.maxent;

import java.io.IOException;
import java.util.ArrayList;

import tools.CSVGenerator;
import tools.Chrono;

public abstract class Learning<T extends Comparable<T>> {
	
	protected CSVGenerator report;
	protected ArrayList<Chrono> chronos;
	
	public Learning() {
		report = new CSVGenerator("data/report" + this.getClass().getSimpleName() + ".csv",
				"Report type:" + this.getClass().getSimpleName());
		report.setAddDate(true);
		report.setAddTitle(true);
		chronos = new ArrayList<>();
		
	}
	
	public abstract void run();
	
	public abstract CSVGenerator report(boolean save) throws IOException;
	
	public CSVGenerator getReport() {
		return report;
	}
	public void setReport(CSVGenerator report) {
		this.report = report;
	}

	public ArrayList<Chrono> getChronos() {
		return chronos;
	}

	public void setChronos(ArrayList<Chrono> chronos) {
		this.chronos = chronos;
	}
	

	
}
