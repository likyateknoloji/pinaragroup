/*******************************************************************************
 * Copyright 2013 Likya Teknoloji
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.likya.myra.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.xmlbeans.GDuration;

import com.likya.commons.utils.DateUtils;
import com.likya.myra.commons.utils.MyraDateUtils;
import com.likya.myra.commons.utils.RestrictedDailyIterator;
import com.likya.myra.jef.core.CoreFactory;
import com.likya.xsd.myra.model.joblist.AbstractJobType;
import com.likya.xsd.myra.model.jobprops.DaysOfMonthDocument.DaysOfMonth;
import com.likya.xsd.myra.model.jobprops.PeriodInfoDocument.PeriodInfo;
import com.likya.xsd.myra.model.jobprops.ScheduleInfoDocument.ScheduleInfo;

public class CopyOfScheduler {

	public static boolean scheduleForNextExecution(AbstractJobType abstractJobType) {

		boolean retValue = true;

		PeriodInfo periodInfo = abstractJobType.getManagement().getPeriodInfo();
		// TODO Special care for daily jobs, may be done compatible with general periodicity
		if (periodInfo == null || periodInfo.getStep() == null || periodInfo.getStep().equals(new GDuration("P1D"))) {
			Calendar selectedSchedule = regularSchedule(abstractJobType);
			if (selectedSchedule != null /*&& selectedSchedule.after(DateUtils.getCalendarInstance())*/) {
				abstractJobType.getManagement().getTimeManagement().getJsActualTime().setStartTime(selectedSchedule);
			} else {
				// yeni zamana kurulmadı, artık çalışmayacak
				retValue = false;
			}
		} else {
			retValue = periodicSchedule(abstractJobType);
		}

		return retValue;
	}

	private static boolean periodicSchedule(AbstractJobType abstractJobType) {

		boolean retValue = true;

		ArrayList<String> errorMessages = new ArrayList<String>();

		Calendar nextPeriodTime = null; //TimeScheduler.forward(abstractJobType, errorMessages);
		
		for(String message : errorMessages) {
			CoreFactory.getLogger().warn(message);
		}
		
		if (nextPeriodTime == null) {
			Calendar bornedCal = abstractJobType.getManagement().getTimeManagement().getJsScheduledTime().getStartTime();
			// System.err.println(c);
			//c.add(Calendar.DAY_OF_MONTH, 1);
			//abstractJobType.getManagement().getTimeManagement().getJsPlannedTime().setStartTime(c);
			// System.err.println(abstractJobType.getManagement().getTimeManagement().getJsPlannedTime().getStartTime());
			Calendar selectedSchedule = regularSchedule(abstractJobType);
			if (selectedSchedule != null /*&& selectedSchedule.after(DateUtils.getCalendarInstance())*/) {
				MyraDateUtils.setTimePart(bornedCal, selectedSchedule);
				abstractJobType.getManagement().getTimeManagement().getJsActualTime().setStartTime(selectedSchedule);
				// yeni zamana kuruldu
			} else {
				// yeni zamana kurulmadı, artık çalışmayacak
				retValue = false;
			}
		}

		return retValue;
	}

	private static Calendar regularSchedule(AbstractJobType abstractJobType) {

		ScheduleInfo scheduleInfo = abstractJobType.getScheduleInfo();
		
		if(scheduleInfo == null) {
			CoreFactory.getLogger().warn("No scheduling rule is defined !");
			return null;
		}
		
		PeriodInfo periodInfo = abstractJobType.getManagement().getPeriodInfo();

//		if (!TimeScheduler.checkMaxCount(periodInfo)) {
//			CoreFactory.getLogger().warn("Execution count exceeded the value defined for maxCount !");
//			return null;
//		}
		
		Calendar jsScheduledTime = abstractJobType.getManagement().getTimeManagement().getJsScheduledTime().getStartTime();

		Calendar selectedSchedule = null;

		ArrayList<Calendar> floatingSchedules = new ArrayList<Calendar>();

		int hourOfPlannedTime = jsScheduledTime.get(Calendar.HOUR_OF_DAY);
		int minuteOfPlannedTime = jsScheduledTime.get(Calendar.MINUTE);
		int secondOfPlannedTime = jsScheduledTime.get(Calendar.SECOND);

		int daysOfWeek[] = scheduleInfo.getDaysOfWeekIntTypeArray();

		RestrictedDailyIterator restrictedDailyIterator = null;

		Calendar restCal = null;

		if (daysOfWeek.length > 0) {
			restrictedDailyIterator = new RestrictedDailyIterator(hourOfPlannedTime, minuteOfPlannedTime, secondOfPlannedTime, daysOfWeek);
			restCal = restrictedDailyIterator.next();
			floatingSchedules.add(restCal);
			CoreFactory.getLogger().debug("Option for daysOfWeek : " + MyraDateUtils.getDate(restCal));
		}

		DaysOfMonth daysOfMonth = scheduleInfo.getDaysOfMonth();

		if (daysOfMonth != null) {

			int dayList[] = daysOfMonth.getDaysArray();

			String firstDay = daysOfMonth.getFirstDayOfMonth();

			String lastDay = daysOfMonth.getLastDayOfMonth();

			if (dayList.length > 0) {
				restrictedDailyIterator = new RestrictedDailyIterator(hourOfPlannedTime, minuteOfPlannedTime, secondOfPlannedTime, dayList);
				restCal = restrictedDailyIterator.next(Calendar.DAY_OF_MONTH);
				floatingSchedules.add(restCal);
				CoreFactory.getLogger().debug("Option for daysOfMonth : " + MyraDateUtils.getDate(restCal));
			}

			if (firstDay != null) {
				int firstDayOfMonth = 1;
				restCal = MyraDateUtils.setTimePart(jsScheduledTime);
				restCal.add(Calendar.MONTH, 1);
				restCal.set(Calendar.DAY_OF_MONTH, firstDayOfMonth);
				floatingSchedules.add(restCal);
				CoreFactory.getLogger().debug("Option for firstDayOfMonth : " + MyraDateUtils.getDate(restCal));
			}

			if (lastDay != null) {
				int lastDayOfMonth = DateUtils.getCalendarInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
				restCal = MyraDateUtils.setTimePart(jsScheduledTime);
				if (DateUtils.getCalendarInstance().get(Calendar.DAY_OF_MONTH) == lastDayOfMonth) {
					restCal.add(Calendar.MONTH, 1);
					lastDayOfMonth = restCal.getActualMaximum(Calendar.DAY_OF_MONTH);
				}
				restCal.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
				floatingSchedules.add(restCal);
				CoreFactory.getLogger().debug("Option for lastDayOfMonth : " + MyraDateUtils.getDate(restCal));
			}

		}

		Calendar[] sortedCals = floatingSchedules.toArray(new Calendar[0]);

		// System.err.println(sortedCals[0].getTime());
		// System.err.println(sortedCals[1].getTime());
		// System.err.println(sortedCals[2].getTime());		

		Arrays.sort(sortedCals);

		// System.err.println(sortedCals[0].getTime());
		// System.err.println(sortedCals[1].getTime());
		// System.err.println(sortedCals[2].getTime());

		// System.err.println(MyraDateUtils.getDate(selectedSchedule.getTime()));

		if (sortedCals.length > 0) {
			selectedSchedule = DateUtils.getCalendarInstance();
			selectedSchedule.setTime(sortedCals[0].getTime());
			if (periodInfo != null) {
				periodInfo.setCounter(BigInteger.valueOf(periodInfo.getCounter().intValue() + 1));
			}
			CoreFactory.getLogger().debug("Minimum of options : " + MyraDateUtils.getDate(selectedSchedule));
		}

		return selectedSchedule;

	}

}
