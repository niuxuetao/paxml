/**
 * This file is part of PaxmlCore.
 *
 * PaxmlCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.test;

import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.paxml.core.PaxmlResource;
import org.paxml.launch.LaunchModel;
import org.paxml.launch.LaunchPoint;
import org.paxml.launch.Paxml;

public class PlanFileTest {
	@Test
	public void test1() {
		LaunchModel model = Paxml.executePlanFile("plan/1.xml", System.getProperties());
		Assert.assertEquals(2, model.getGroups().size());
		List<LaunchPoint> points = model.getLaunchPoints(false, -1);
		Assert.assertEquals(4, points.size());

		Properties fs = new Properties();
		Properties ps = new Properties();
		LaunchPoint p = new LaunchPoint(null, PaxmlResource.createFromPath("classpath:plan/Generic.xml"), null, System.getProperties(), ps, fs, 0, -1);

		ps.clear();
		ps.put("p1", "1");
		fs.clear();
		fs.setProperty("f1", "11");
		fs.setProperty("f2", "11");
		assertPoint(p, points.get(0));

		fs.clear();
		fs.setProperty("f1", "11");
		fs.setProperty("f2", "12");
		assertPoint(p, points.get(1));

		ps.clear();
		fs.clear();
		fs.setProperty("f1", "21");
		fs.setProperty("f2", "21");
		assertPoint(p, points.get(2));

		fs.clear();
		fs.setProperty("f1", "21");
		fs.setProperty("f2", "22");
		assertPoint(p, points.get(3));
	}

	@Test
	public void test2() {
		LaunchModel model = Paxml.executePlanFile("plan/2.xml", System.getProperties());
		Assert.assertEquals(2, model.getGroups().size());
		List<LaunchPoint> points = model.getLaunchPoints(false, -1);
		Assert.assertEquals(2, points.size());

		Properties fs = new Properties();
		Properties ps = new Properties();
		LaunchPoint p = new LaunchPoint(null, PaxmlResource.createFromPath("classpath:plan/Generic.xml"), null, System.getProperties(), ps, fs, 0, -1);

		fs.setProperty("f3", "3");

		assertPoint(p, points.get(0));
		assertPoint(p, points.get(1));

	}

	@Test
	public void test3() {
		LaunchModel model = Paxml.executePlanFile("plan/3.xml", System.getProperties());
		Assert.assertEquals(2, model.getGroups().size());
		List<LaunchPoint> points = model.getLaunchPoints(false, -1);
		Assert.assertEquals(4, points.size());

		Properties fs = new Properties();
		Properties ps = new Properties();
		LaunchPoint p = new LaunchPoint(null, PaxmlResource.createFromPath("classpath:plan/Generic.xml"), null, System.getProperties(), ps, fs, 0, -1);

		fs.clear();
		fs.setProperty("f1", "11");
		fs.setProperty("f2", "11");
		fs.setProperty("f3", "3");
		fs.setProperty("f4", "4");
		assertPoint(p, points.get(0));

		fs.clear();
		fs.setProperty("f1", "11");
		fs.setProperty("f2", "12");
		fs.setProperty("f3", "3");
		fs.setProperty("f4", "4");
		assertPoint(p, points.get(1));

		fs.clear();
		fs.setProperty("f1", "21");
		fs.setProperty("f2", "21");
		fs.setProperty("f3", "3");
		fs.setProperty("f4", "4");
		assertPoint(p, points.get(2));

		fs.clear();
		fs.setProperty("f1", "21");
		fs.setProperty("f2", "22");
		fs.setProperty("f3", "3");
		fs.setProperty("f4", "4");
		assertPoint(p, points.get(3));
	}

	@Test
	public void test4() {
		LaunchModel model = Paxml.executePlanFile("plan/4.xml", System.getProperties());
		Assert.assertEquals(4, model.getGroups().size());
		List<LaunchPoint> points = model.getLaunchPoints(false, -1);
		Assert.assertEquals(10, points.size());

		Properties fs = new Properties();
		Properties ps = new Properties();

		LaunchPoint p = new LaunchPoint(null, PaxmlResource.createFromPath("classpath:plan/Generic.xml"), null, System.getProperties(), ps, fs, 0, -1);

		int i = 0;

		fs.clear();
		fs.setProperty("evaLevel", "3");
		fs.setProperty("taskId", "834");
		fs.setProperty("enableTasks", "803a");
		fs.setProperty("enterCallback", "EvaAddressBookAddCallback");
		assertPoint(p, points.get(i++));

		fs.setProperty("evaLevel", "2");
		assertPoint(p, points.get(i++));

		fs.setProperty("evaLevel", "1");
		assertPoint(p, points.get(i++));

		// ///////////////////////

		fs.clear();
		fs.setProperty("evaLevel", "3");
		fs.setProperty("taskId", "835");
		fs.setProperty("enableTasks", "834");
		fs.setProperty("enterCallback", "EvaAddressBookEditCallback");
		assertPoint(p, points.get(i++));

		fs.setProperty("evaLevel", "2");
		assertPoint(p, points.get(i++));

		fs.setProperty("evaLevel", "1");
		assertPoint(p, points.get(i++));

		// ///////////////////////////

		fs.clear();
		fs.setProperty("evaLevel", "3");
		fs.setProperty("taskId", "836");
		fs.setProperty("enableTasks", "834");
		fs.setProperty("enterCallback", "EvaAddressBookRemoveCallback");
		assertPoint(p, points.get(i++));

		fs.setProperty("evaLevel", "2");
		assertPoint(p, points.get(i++));

		fs.setProperty("evaLevel", "1");
		assertPoint(p, points.get(i++));

		// /////////////////////////////////

		fs.clear();
		p = new LaunchPoint(null, PaxmlResource.createFromPath("classpath:plan/Another.xml"), null, System.getProperties(), ps, null, 0, -1);
		assertPoint(p, points.get(i++));

	}

	@Test
	public void test5() {
		LaunchModel model = Paxml.executePlanFile("plan/5.xml", System.getProperties());
		Assert.assertEquals(0, model.getGroups().size());
		List<LaunchPoint> points = model.getLaunchPoints(false, -1);
		Assert.assertEquals(4, points.size());

		Properties fs = new Properties();

		Properties ps = new Properties();
		ps.put("gp1", "1");
		ps.put("gp2", "2");

		LaunchPoint p = new LaunchPoint(null, PaxmlResource.createFromPath("classpath:plan/Generic.xml"), null, System.getProperties(), ps, fs, 0, -1);

		int i = 0;

		fs.clear();
		fs.setProperty("f1", "11");
		fs.setProperty("f2", "x2");
		assertPoint2(p, points.get(i++));

		fs.setProperty("f1", "12");
		assertPoint2(p, points.get(i++));

		p = new LaunchPoint(null, PaxmlResource.createFromPath("classpath:plan/Another.xml"), null, System.getProperties(), ps, fs, 0, -1);
		fs.setProperty("f1", "11");
		assertPoint2(p, points.get(i++));

		// / skip checking the rest

	}

	private void assertPoint(LaunchPoint ep, LaunchPoint ap) {
		Assert.assertEquals(ep.getResource().getName(), ap.getResource().getName());
		Assert.assertEquals(ep.getFactors(), ap.getFactors());
		Assert.assertEquals(ep.getProperties(), ap.getProperties());

	}

	private void assertPoint2(LaunchPoint ep, LaunchPoint ap) {
		Assert.assertEquals(ep.getResource().getName(), ap.getResource().getName());
		Assert.assertEquals(ep.getFactors(), ap.getFactors());
		Assert.assertEquals(ep.getEffectiveProperties(true), ap.getEffectiveProperties(true));

	}
}
