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
package org.paxml.core;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
/*
import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor;
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorWriter;
import org.apache.ivy.plugins.resolver.URLResolver;
*/
public class DependencyResolver {
	/*
	public static void main(String[] x)throws Exception{
		System.out.println(resolveArtifact("org.paxml", "paxml-example", "1.7", null));
	}
	
	public static List<File> resolveArtifact(String groupId, String artifactId, String version, File dir) throws IOException, ParseException {
		// creates clear ivy settings
		IvySettings ivySettings = new IvySettings();
		// url resolver for configuration of maven repo
		URLResolver resolver = new URLResolver();
		resolver.setM2compatible(true);
		resolver.setName("central");
		// you can specify the url resolution pattern strategy
		resolver.addArtifactPattern("http://repo1.maven.org/maven2/[organisation]/[module]/[revision]/[artifact](-[revision]).[ext]");
		// adding maven repo resolver
		ivySettings.addResolver(resolver);
		// set to the default resolver
		ivySettings.setDefaultResolver(resolver.getName());
		// creates an Ivy instance with settings
		Ivy ivy = Ivy.newInstance(ivySettings);

		File ivyfile = File.createTempFile("ivy", ".xml");
		ivyfile.deleteOnExit();

		DefaultModuleDescriptor md = DefaultModuleDescriptor.newDefaultInstance(ModuleRevisionId.newInstance(groupId, artifactId + "-caller", "working"));

		DefaultDependencyDescriptor dd = new DefaultDependencyDescriptor(md, ModuleRevisionId.newInstance(groupId, artifactId, version), false, false, true);
		md.addDependency(dd);
		
		// creates an ivy configuration file
		XmlModuleDescriptorWriter.write(md, ivyfile);

		String[] confs = { "default" };
		ResolveOptions resolveOptions = new ResolveOptions().setConfs(confs);
		
		// init resolve report
		ResolveReport report = ivy.resolve(ivyfile.toURL(), resolveOptions);

		ArtifactDownloadReport[] downloads = report.getAllArtifactsReports();
		List<File> files = new ArrayList<File>(downloads.length);
		for (ArtifactDownloadReport d : downloads) {
			files.add(d.getLocalFile());
		}
		return files;
	}
	*/
}
