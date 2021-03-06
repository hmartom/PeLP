/*
	Copyright 2011-2012 Fundació per a la Universitat Oberta de Catalunya

	This file is part of PeLP (Programming eLearning Plaform).

    PeLP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    PeLP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package edu.uoc.pelp.test;

import edu.uoc.pelp.conf.IPelpConfiguration;
import edu.uoc.pelp.engine.PELPEngine;
import edu.uoc.pelp.engine.activity.ActivityID;
import edu.uoc.pelp.engine.activity.TestID;
import edu.uoc.pelp.engine.auth.EngineAuthManager;
import edu.uoc.pelp.engine.campus.*;
import edu.uoc.pelp.engine.campus.UOC.ClassroomID;
import edu.uoc.pelp.engine.campus.UOC.Semester;
import edu.uoc.pelp.engine.campus.UOC.SubjectID;
import edu.uoc.pelp.engine.campus.UOC.UserID;
import edu.uoc.pelp.engine.deliver.Deliver;
import edu.uoc.pelp.engine.deliver.DeliverID;
import edu.uoc.pelp.test.conf.PCPelpConfiguration;
import edu.uoc.pelp.test.engine.activity.LocalActivityManager;
import edu.uoc.pelp.test.engine.campus.TestUOC.LocalCampusConnection;
import edu.uoc.pelp.test.engine.delivery.LocalDeliverManager;
import edu.uoc.pelp.test.resource.CodeSamples;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Perform all tests over the PeLP platform
 * @author Xavier Baró
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({edu.uoc.pelp.test.engine.campus.TestUOC.LocalAuthManager_NotAuth.class,
                     edu.uoc.pelp.test.engine.campus.TestUOC.LocalAuthManager_Campus.class,
                     edu.uoc.pelp.test.engine.campus.TestUOC.LocalAuthManager_Student.class,
                     edu.uoc.pelp.test.engine.aem.CodeProject_Basics.class,
                     edu.uoc.pelp.test.engine.aem.CodeProject_Compile.class,
                     edu.uoc.pelp.test.engine.aem.CodeProject_Execute.class,
                     edu.uoc.pelp.test.engine.activity.LocalActivityManager_Basic.class,
                     edu.uoc.pelp.test.engine.activity.LocalDAOActivityManager_Basic.class,
                     edu.uoc.pelp.test.engine.Engine_InfoRetrival.class,
                     edu.uoc.pelp.test.model.dao.UOC.TDAO_Semester.class,
                     edu.uoc.pelp.test.model.dao.TDAO_Activity.class,
                     edu.uoc.pelp.test.model.dao.TDAO_Deliver.class}
        )
public class TestPeLP {
    
    /**
     * Access to an LocalAuthManager object which uses the  dummy local version of campus data
     */
    public static EngineAuthManager localDummyAuthManager=new EngineAuthManager();
    
    /**
     * Access to configuration parameters for developing environments
     */
    public static IPelpConfiguration localConfiguration=new PCPelpConfiguration();

    /**
     * Access to the bussines object
     */
    public static String _bussines;
    
    /**
     * Acces to code samples
     */
    public static CodeSamples _codeSamples=new CodeSamples();
    
    /** 
     * Obtain the engine object to be tested.
     */
    public static PELPEngine getEngineObject() {
        
        PELPEngine engine=new PELPEngine();
        
        // Assign a Campus connection
        engine.setCampusConnection(new LocalCampusConnection()); // Dummy campus connection
        
        // Assign an Activity Manager
        engine.setActivityManager(new LocalActivityManager()); // Local activity manager
        
        // Assign a Deliver Manager
        engine.setDeliverManager(new LocalDeliverManager()); // Local deliver manager
        
        // Assign a configuration object
        engine.setSystemConfiguration(new PCPelpConfiguration()); // Class with local values for each machine
        
        return engine;
    }
    
    public static Semester getDummySemester() {
        return new Semester("20111");
    }
    
    public static ISubjectID getDummySubjectID() {
        return new SubjectID("05.554",getDummySemester());
    }
    
    public static IUserID getDummyUserID() {
        return new UserID("1234567");
    }
    
    public static ActivityID getDummyActivityID(Long idx) {
        if(idx==null) {
            idx=new Long(1l);
        }
        return new ActivityID(getDummySubjectID(),idx);
    }
    
    public static DeliverID getDummyDeliverID() {
        return new DeliverID(getDummyUserID(),getDummyActivityID(null),1);
    }
    
    public static Deliver getDummyDeliver() {
        return new Deliver(getDummyDeliverID(),TestPeLP.createTemporalFolder("TempDeliver"));
    }
    
    public static TestID getDummyTestID() {
        return new TestID(getDummyActivityID(null),1);
    }
        
    public static LocalCampusConnection getLocal(ICampusConnection campusConnection) {
        assert(LocalCampusConnection.class.isAssignableFrom(campusConnection.getClass()));
        return (LocalCampusConnection)campusConnection;
    }
    
    public static Person getUser(ICampusConnection campusConnection,int pos) {
        return getLocal(campusConnection).getTestPersonByPos(pos);
    }
    
    public static Subject getSubject(ICampusConnection campusConnection,int pos) {
        return getLocal(campusConnection).getTestSubjectByPos(pos);
    }
    
    public static Classroom getClassroom(Subject subject,int posClassroom) {
        Classroom ret=null;
        IClassroomID id=new ClassroomID((SubjectID)subject.getID(),posClassroom);
        if(subject.getClassrooms()!=null) {
            ret=subject.getClassrooms().get(id);
        }
        return ret;
    }
    
    public static Classroom getClassroom(ICampusConnection campusConnection,int posSubject,int posClassroom) {
        Subject s=getSubject(campusConnection,posSubject);
        return getClassroom(s,posClassroom);
    }
    
    public static File createTemporalFolder(String path) {
        return createTemporalFolder(path,null);
    }
    public static File createTemporalFolder(String path,File rootPath) {
        // Remove extra separators
        if(path.charAt(0)==File.separatorChar) {
            path.substring(1);
        }
        
        // Create a temporal folder
        File tmpPath;
        if(rootPath==null) {
            tmpPath=new File(System.getProperty("java.io.tmpdir")+ "PELP" + File.separator + path);  
        } else {
            tmpPath=new File(rootPath.getAbsolutePath() + File.separator + path);  
        }
        if(!tmpPath.exists()) {
            Assert.assertTrue("Create temporal path",tmpPath.mkdirs());
        }
        Assert.assertTrue("Temporal folder exists",tmpPath.exists() && tmpPath.isDirectory() && tmpPath.canWrite() && tmpPath.canRead() && tmpPath.canExecute());
        tmpPath.deleteOnExit();
        
        return tmpPath;
    }
    
    public static File createTempFile(File filePath,File rootPath) {
        File fullPath;
        
        try {
            if(filePath==null || rootPath==null) {
                return null;
            }            
            fullPath=new File(rootPath.getAbsolutePath() + File.separator + filePath.getPath());
            if(!fullPath.getParentFile().exists()) {
                if(!fullPath.getParentFile().mkdirs()) {
                    return null;
                }
            }
            fullPath.createNewFile();
            fullPath.deleteOnExit();
        } catch (IOException ex) {
            fullPath=null;
        }
        
        return fullPath;
    }
    
    public static boolean createFile(File rootPath,String fileName,String content) {
        try {
            File file=new File(rootPath.getAbsolutePath() + File.separator + fileName);
            PrintWriter writer=new PrintWriter(file);
            writer.print(content);
            writer.close();
        } catch (FileNotFoundException ex) {
            return false;
        }
        
        return true;
    }
    
    public static boolean deleteFilePath(File rootPath) {
        
        if(!rootPath.isDirectory()) {
            return rootPath.delete();
        }
        
        boolean retVal=true;
        for(File f:rootPath.listFiles()) {
            retVal&=deleteFilePath(f);
        }
        return retVal;
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        
    }

    @After
    public void tearDown() throws Exception {
    }  
}
