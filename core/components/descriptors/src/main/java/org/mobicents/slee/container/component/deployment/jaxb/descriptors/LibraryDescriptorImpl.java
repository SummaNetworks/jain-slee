package org.mobicents.slee.container.component.deployment.jaxb.descriptors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.slee.ComponentID;
import javax.slee.management.LibraryID;

import org.mobicents.slee.container.component.deployment.jaxb.descriptors.library.MJar;
import org.mobicents.slee.container.component.deployment.jaxb.descriptors.library.MLibrary;
import org.mobicents.slee.container.component.deployment.jaxb.descriptors.library.MLibraryJar;
import org.w3c.dom.Document;

/**
 * 
 * LibraryDescriptorImpl.java
 *
 * <br>Project:  mobicents
 * <br>3:52:21 AM Jan 30, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class LibraryDescriptorImpl extends JAXBBaseUtilityClass  {

  private int index;
  private MLibraryJar libraryJar;
  
  private MLibrary library;
  
  private String description;
  private List<MJar> jars = new ArrayList<MJar>();
  private LibraryID libraryID;
  
  public LibraryDescriptorImpl(Document doc)
  {
    super(doc);
  }
  
  public LibraryDescriptorImpl(Document doc, org.mobicents.slee.container.component.deployment.jaxb.slee11.library.LibraryJar libraryJar11, int index)
  {
    this.libraryJar = new MLibraryJar(libraryJar11);
    this.index = index;
  }

  @Override
  public void buildDescriptionMap()
  {
    this.library = this.libraryJar.getLibrary().get(index);
    this.description = this.library.getDescription();
    
    this.jars = this.library.getJar();
    
    libraryID = new LibraryID(library.getLibraryName(), library.getLibraryVendor(),library.getLibraryVersion());
  }

  @Override
  public Object getJAXBDescriptor()
  {
    return this.libraryJar;
  }
  
  public MLibrary getLibrary()
  {
    return library;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public List<MJar> getJars()
  {
    return jars;
  }
  
  public LibraryID getLibraryID() {
	return libraryID;
  }
  
  public Set<ComponentID> getDependenciesSet() {
	// TODO Auto-generated method stub
	return null;
  }
  
}
