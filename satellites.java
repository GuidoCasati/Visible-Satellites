/**
 * 
 */
package satellites;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author 		Guido Casati
 * @version		2.0
 * @date		2018
 */
public class satellites {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// start input session
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String sInput, sSeparator =",";

		System.out.print("Please enter the location of the input file: ");
		try {
			sInput = br.readLine();
			while (sInput != null) {
				// defining timestamps format
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SS");

				// reading the entries in the .dat file
				try {
					BufferedReader bufferFromFile = new BufferedReader(new FileReader(sInput));
					String entry; 
					
					// creating a list of pairs composed by time and boolean indicating if starting or ending time
					List<Pair> timestampsPairs = new ArrayList<Pair>();

					//reading the entries from the buffer
					while ((entry = bufferFromFile.readLine()) != null) {
						//splitting on the comma separator
						String[] saLine = entry.split(sSeparator);

						//creating pairs from timestamps
						Date timeStart;
						Date timeEnd;
						
						try {						
							//parsing the time strings to date objects
							timeStart = simpleDateFormat.parse(saLine[0]);
							timeEnd = simpleDateFormat.parse(saLine[1]);

							//creating pairs
							Pair pair1 = new Pair(timeStart, true);
							Pair pair2 = new Pair(timeEnd, false);

							//populating the list of pairs 
							timestampsPairs.add(pair1);
							timestampsPairs.add(pair2);

						} catch (ParseException e) {
							//handling parse exception
							System.out.println(e.getMessage());
							break;
						}
					}
					bufferFromFile.close();
					
					// Sorting the list by timestamp
					Collections.sort(timestampsPairs, new Comparator<Pair>() {
						@Override
						public int compare(Pair pairA, Pair pairB) {
							return pairA.getDate().compareTo(pairB.getDate());
						}
					});

					// counting the max number of visible satellites within a time range
					
					Integer counter = 0, iMaxCounter = 0, iExtraCounter = 0;
					Boolean bIsStart, bPreviousCheck = null;
					List<Pair> Ranges = new ArrayList<Pair>();
					Date dStartTime = null, dEndTime = null, dPreviousTimestamp = null, dCurrentTimestamp=null;
					
					// looping in the timestamp pairs 
					for (Pair p : timestampsPairs) {
						bIsStart = p.getStartEnd();
						dCurrentTimestamp = p.getDate();

						// incrementing the counter if start time, decrementing otherwise
						if (bIsStart)
							counter++;
						else
							counter--;

						// updating the ranges list, start time and end time whenever a new visibility range is found
						if (iMaxCounter < counter) {
							iMaxCounter = counter;
							Ranges.clear();
							Ranges.add(p);
							dStartTime = dCurrentTimestamp;
						} else if (!bIsStart && counter.equals(iMaxCounter - 1)) {
							Ranges.add(p);
							dEndTime = dCurrentTimestamp;
						} else if (bIsStart && counter.equals(iMaxCounter)) {
							Ranges.add(p);
							dStartTime = dCurrentTimestamp;
						}

						// handling the case satellites have same start or end time stamp
						if (dCurrentTimestamp == dPreviousTimestamp) {
							if (bPreviousCheck == true)
								iExtraCounter = counter;
							else if (bPreviousCheck == false && bIsStart)
								iExtraCounter = counter + 1;
							else if (bPreviousCheck == false && !bIsStart) {
								dEndTime = dCurrentTimestamp;
								iExtraCounter = counter + 2;
							}
						}

						// updating the max counter whenever the extra counter is higher
						if (iExtraCounter > iMaxCounter) {
							iMaxCounter = iExtraCounter;
							Ranges.clear();
							Ranges.add(new Pair(dStartTime, true));
							Ranges.add(new Pair(dEndTime, false));
						}
						//updating previous timestamp holder
						dPreviousTimestamp = dCurrentTimestamp;
						bPreviousCheck = bIsStart;
					}

					// printing the result on screen
					String sStartRange = null, sEndRange = null;
					Integer iPrintCounter = 0;
					for (Pair p : Ranges) {
						boolean bIsStartRange = p.getStartEnd();
						Date dRangeTime = p.getDate();

						if (bIsStartRange)
							sStartRange = simpleDateFormat.format(dRangeTime);
						else
							sEndRange = simpleDateFormat.format(dRangeTime);

						iPrintCounter++;
						
						if (iPrintCounter == 2) {
							System.out.println(sStartRange + "-" + sEndRange + ";" + iMaxCounter);
							iPrintCounter = 0;
						}
					}
					break;

				} catch (FileNotFoundException e) {
					System.out.println("File does not exist.");
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static class Pair {
		private Date date;

		private Boolean isStart;

		public Pair(Date date, Boolean isStart) {
			super();
			this.date = date;
			this.isStart = isStart;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public Boolean getStartEnd() {
			return isStart;
		}

		public void setStartEnd(Boolean isStart) {
			this.isStart = isStart;
		}
	}
}
