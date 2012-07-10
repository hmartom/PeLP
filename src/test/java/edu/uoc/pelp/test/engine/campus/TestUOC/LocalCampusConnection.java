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
package edu.uoc.pelp.test.engine.campus.TestUOC;

import edu.uoc.pelp.engine.campus.UOC.ClassroomID;
import edu.uoc.pelp.engine.campus.UOC.Semester;
import edu.uoc.pelp.engine.campus.UOC.SubjectID;
import edu.uoc.pelp.engine.campus.UOC.UserID;
import edu.uoc.pelp.engine.campus.*;
import edu.uoc.pelp.exception.AuthPelpException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a dummy class simulating the campus access for the 
 * Universitat Oberta de Catalunya (UOC). All the data of this class is
 * available locally, without DB or network connection. Access to information
 * is restricted depending on the user roles.
 * @author Xavier Baró
 */
public class LocalCampusConnection implements ICampusConnection{

    /**
     * Current authenticated user (null if no authenticated user)
     */
    private UserID _userID=null;
    
    /**
     * List of available persons
     */
    private HashMap<UserID,Person> _dummyUsers=new HashMap<UserID,Person>();
    
    /**
     * List of available subjects
     */
    private HashMap<SubjectID,Subject> _dummySubjects=new HashMap<SubjectID,Subject>();
        
    /** 
     * List of available semesters 
     **/
    private HashMap<String,Semester> _dummySemesters=new HashMap<String,Semester>();
    
    /**
     * Flat to simulate a connection from campus computer.
     */
    private boolean _campusConnection=false;
        
    /**
     * Array to access Subjects by Pos (For Testing purposes)
     */
    private Subject[] _testAccessSubjects=null;
        
    /**
     * Array to access persons by Pos (For Testing purposes)
     */
    private Person[] _testAccessPersons=null;
    
    public LocalCampusConnection() {
        createDummyData();
    }
        
    public boolean isUserAuthenticated() {
        return (_userID!=null);
    }

    public ISubjectID[] getUserSubjects() throws AuthPelpException {
        return getUserSubjects(null);
    }
    
    public ISubjectID[] getUserSubjects(UserRoles userRole) throws AuthPelpException {
        if(!isUserAuthenticated()) {
            throw new AuthPelpException();
        }
        
        // Search subjects of the current user with the given filter
        ArrayList<ISubjectID> retList=new ArrayList<ISubjectID>();
        if(_dummySubjects!=null) {
            for(Subject s:_dummySubjects.values()) {
                if(userRole!=null) {
                    if(isRole(userRole,s.getID())) {
                        retList.add(s.getID());
                    }                     
                } else {
                    if(isRole(UserRoles.Student,s.getID()) || isRole(UserRoles.Teacher,s.getID())) {
                        retList.add(s.getID());
                    }
                }
            }
        }
        
        // Create an array from the list of subjects
        ISubjectID[] retArray=new ISubjectID[retList.size()];
        retList.toArray(retArray);
        
        return retArray;
    }
    
    public IClassroomID[] getUserClassrooms() throws AuthPelpException {
        return getUserClassrooms(null);
    }
    
    public IClassroomID[] getUserClassrooms(UserRoles userRole) throws AuthPelpException {
        if(!isUserAuthenticated()) {
            throw new AuthPelpException();
        }
        
        // Search classrooms of the current user with the given filter
        ArrayList<IClassroomID> retList=new ArrayList<IClassroomID>();
        if(_dummySubjects!=null) {
            for(Subject s:_dummySubjects.values()) {
                if(s.getClassrooms()!=null) {
                    for(Classroom c:s.getClassrooms().values()) {
                        if(userRole!=null) {
                            if(isRole(userRole,c.getClassroomID())) {
                                retList.add(c.getClassroomID());
                            }                     
                        } else {
                            if(isRole(UserRoles.Student,c.getClassroomID()) || isRole(UserRoles.Teacher,c.getClassroomID())) {
                                retList.add(c.getClassroomID());
                            }
                        }
                    }
                }
            }
        }
        
        // Create an array from the list of subjects
        IClassroomID[] retArray=new IClassroomID[retList.size()];
        retList.toArray(retArray);
        
        return retArray;
    }

    public IClassroomID[] getSubjectClassrooms(ISubjectID subject, UserRoles userRole) throws AuthPelpException {
        
        assert(subject!=null);
        
        if(!isUserAuthenticated()) {
            throw new AuthPelpException();
        }
        
        // Get the subject
        Subject subjectData=_dummySubjects.get((SubjectID)subject);
        
        // Search classrooms of the current user with the given filter
        ArrayList<IClassroomID> retList=new ArrayList<IClassroomID>();        
        if(subjectData.getClassrooms()!=null) {
            for(Classroom c:subjectData.getClassrooms().values()) {
                if(userRole!=null) {
                    if(isRole(userRole,c.getClassroomID())) {
                        retList.add(c.getClassroomID());
                    }                     
                } else {
                    if(isRole(UserRoles.Student,c.getClassroomID()) || isRole(UserRoles.Teacher,c.getClassroomID())) {
                        retList.add(c.getClassroomID());
                    }
                }
            }
        }
                
        // Create an array from the list of subjects
        IClassroomID[] retArray=new IClassroomID[retList.size()];
        retList.toArray(retArray);
        
        return retArray;
    }
    
    public IUserID getUserID() throws AuthPelpException {
        if(!isUserAuthenticated()) {
            throw new AuthPelpException();
        }
        return _userID;
    }
    
    public boolean isRole(UserRoles role, ISubjectID subject)  throws AuthPelpException{
        assert(role!=null);
        assert(subject!=null);
        return isRole(role,subject,getUserID());
    }

    public boolean isRole(UserRoles role, ISubjectID subject, IUserID userID) throws AuthPelpException {
        boolean retVal=false;
        
        // Parameters cannot be null
        assert(role!=null);
        assert(subject!=null);
        assert(userID!=null);
        
        // Check the most general verification
        if(role==UserRoles.CampusUser) {
            return isUserAuthenticated();
        }
        
        // In the rest of the cases, user must be authenticated
        if(!isUserAuthenticated()) {
            return false;
        }
               
        // Get the subject
        Subject subjectData=_dummySubjects.get((SubjectID)subject);
        
        // Perform custom checks for each role
        switch(role) {
            case CampusUser:
                // Never arrives to this option
                assert(false);
                break;
            case Student:                    
                retVal=false;
                if(subjectData.getClassrooms()!=null) {
                    for(Classroom c:subjectData.getClassrooms().values()) {
                        if(c.getStudents().containsKey(userID)) {
                            retVal=true;
                            break;
                        }
                    }
                }
                break;
            case Teacher:
                retVal=false;
                if(subjectData.getClassrooms()!=null) {
                    for(Classroom c:subjectData.getClassrooms().values()) {
                        if(c.getTeachers().containsKey(userID)) {
                            retVal=true;
                            break;
                        }
                    }
                }
                break;
            case MainTeacher:
                retVal=false;
                if(subjectData.getMainTeachers()!=null) {
                    retVal=subjectData.getMainTeachers().containsKey(userID);
                }
                break;  
        }            
        
        // Security Check: Only teachers can ask information for other users. 
        if(!userID.equals(getUserID())) {
            if(!isRole(UserRoles.MainTeacher,subject,getUserID()) &&
               !isRole(UserRoles.Teacher,subject,getUserID())) {
               //throw new AuthPelpException("Not enough rights for this query.");
            }
        }
        
        return retVal;
    }

    public boolean isRole(UserRoles role, IClassroomID classroom)  throws AuthPelpException{
        assert(role!=null);
        assert(classroom!=null);
        return isRole(role,classroom,getUserID());
    }

    public boolean isRole(UserRoles role, IClassroomID classroom, IUserID userID) throws AuthPelpException {
        boolean retVal=false;
        
        // Parameters cannot be null
        assert(role!=null);
        assert(classroom!=null);
        assert(userID!=null);
        
        
        // Check the most general verification
        if(role==UserRoles.CampusUser) {
            return isUserAuthenticated();
        }
        
        // In the rest of the cases, user must be authenticated
        if(!isUserAuthenticated()) {
            return false;
        }
        
        // Get the classroom
        Classroom classroomData=getClassroom(classroom);
        
        // Perform custom checks for each role
        switch(role) {
            case CampusUser:
                // Never arrives to this option
                break;
            case Student:                    
                retVal=classroomData.getStudents().containsKey(userID);
                break;
            case Teacher:
                retVal=classroomData.getTeachers().containsKey(userID);
                break;
            case MainTeacher:
                retVal=classroomData.getSubjectRef().getMainTeachers().containsKey(userID);
                break;  
        }            
        
        return retVal;
    }
    
    public boolean isCampusConnection() {
        return _campusConnection;
    }

    public IUserID[] getRolePersons(UserRoles role, ISubjectID subject) throws AuthPelpException {
        
        assert(role!=null);
        assert(subject!=null);
        
        // If role is incorrect, return null
        if (role==UserRoles.CampusUser) {
            return null;
        }
        
        // Get the subject
        Subject subjectData=_dummySubjects.get((SubjectID)subject);
                
        // Create the persons array
        ArrayList<IUserID> personsList=new ArrayList<IUserID>();
        
        // Perform custom checks for each role
        switch(role) {
            case CampusUser:
                // Never arrives to this option
                assert(false);
                break;
            case Student:  
                // Filter the results. An student cannot retrieve the rest of students.
                if(isRole(UserRoles.Student,subject)) {
                    personsList.add(getUserID());
                } else if(isRole(UserRoles.Teacher,subject) || isRole(UserRoles.MainTeacher,subject)) {
                    if(subjectData.getClassrooms()!=null) {
                        for(Classroom c:subjectData.getClassrooms().values()) {
                            if(c.getStudents()!=null) {
                                for(Person p:c.getStudents().values()) {
                                    personsList.add(p.getUserID());
                                }
                            }
                        }
                    }
                }
                break;
            case Teacher:
                // Filter the results. Teachers information can only be retrieved by subject members.
                if(isRole(UserRoles.Student,subject) || 
                   isRole(UserRoles.Teacher,subject) || 
                   isRole(UserRoles.MainTeacher,subject)) {
                    if(subjectData.getClassrooms()!=null) {
                        for(Classroom c:subjectData.getClassrooms().values()) {
                            if(c.getTeachers()!=null) {
                                for(Person p:c.getTeachers().values()) {
                                    personsList.add(p.getUserID());
                                }
                            }
                        }
                    }
                }
                break;
            case MainTeacher:
                // Filter the results. Teachers information can only be retrieved by subject members.
                if(isRole(UserRoles.Student,subject) || 
                   isRole(UserRoles.Teacher,subject) || 
                   isRole(UserRoles.MainTeacher,subject)) {
                    if(subjectData.getMainTeachers()!=null) {
                        for(Person p:subjectData.getMainTeachers().values()) {
                            personsList.add(p.getUserID());
                        }
                    }
                }
                break;  
        }       
        
        // Create the output array
        IUserID[] retArray=new IUserID[personsList.size()];
        personsList.toArray(retArray);
        
        return retArray;
    }

    public IUserID[] getRolePersons(UserRoles role, IClassroomID classroom) throws AuthPelpException {
        
        assert(role!=null);
        assert(classroom!=null);
        
        // If role is incorrect, return null
        if (role==UserRoles.CampusUser) {
            return null;
        }
        
        // Get the classroom
        Classroom classroomData=getClassroom(classroom);
                
        // Create the persons array
        ArrayList<IUserID> personsList=new ArrayList<IUserID>();
        
        // Perform custom checks for each role
        switch(role) {
            case CampusUser:
                // Never arrives to this option
                assert(false);
                break;
            case Student:  
                // Filter the results. An student cannot retrieve the rest of students.
                if(isRole(UserRoles.Student,classroom)) {
                    personsList.add(getUserID());
                } else if(isRole(UserRoles.Teacher,classroom) || isRole(UserRoles.MainTeacher,classroom)) {
                    if(classroomData.getStudents()!=null) {
                        for(Person p:classroomData.getStudents().values()) {
                            personsList.add(p.getUserID());
                        }
                    }
                }
                break;
            case Teacher:
                // Filter the results. Teachers information can only be retrieved by subject members.
                if(isRole(UserRoles.Student,classroom) || 
                   isRole(UserRoles.Teacher,classroom) || 
                   isRole(UserRoles.MainTeacher,classroom)) {
                    if(classroomData.getTeachers()!=null) {
                        for(Person p:classroomData.getTeachers().values()) {
                            personsList.add(p.getUserID());
                        }
                    }
                }
                break;
            case MainTeacher:
                // Filter the results. Teachers information can only be retrieved by subject members.
                if(isRole(UserRoles.Student,classroom) || 
                   isRole(UserRoles.Teacher,classroom) || 
                   isRole(UserRoles.MainTeacher,classroom)) {
                    if(classroomData.getSubjectRef()!=null) {
                        if(classroomData.getSubjectRef().getMainTeachers()!=null) {
                            for(Person p:classroomData.getSubjectRef().getMainTeachers().values()) {
                                personsList.add(p.getUserID());
                            }
                        }
                    }
                }
                break;  
        }       
        
        // Create the output array
        IUserID[] retArray=new IUserID[personsList.size()];
        personsList.toArray(retArray);
        
        return retArray;
    }

    public boolean hasLabSubjects(ISubjectID subject) throws AuthPelpException {
        
        assert(subject!=null);
        
        Subject s=getSubjectData(subject);
        return s.getChildSubjects()!=null;
    }

    public ISubjectID[] getLabSubjects(ISubjectID subject) throws AuthPelpException {
        
        assert(subject!=null);
        
        ArrayList<ISubjectID> labsList=new ArrayList<ISubjectID>();
        
        // Get the list of labs
        Subject s=getSubjectData(subject);            
        if(s!=null) {         
            if(s.getChildSubjects()!=null) {
                for(ISubjectID labID:s.getChildSubjects().values()) {
                    labsList.add(labID);
                }
            }
        }
        
        // Create the output array
        ISubjectID[] retList=new ISubjectID[labsList.size()];
        labsList.toArray(retList);
        
        return retList;
    }
    
    public boolean hasEquivalentSubjects(ISubjectID subject) throws AuthPelpException {
        
        assert(subject!=null);
        
        Subject s=getSubjectData(subject);
        return s.getEquivalentSubjects()!=null;
    }

    public ISubjectID[] getEquivalentSubjects(ISubjectID subject) throws AuthPelpException {
        
        assert(subject!=null);
        
        ArrayList<ISubjectID> eqList=new ArrayList<ISubjectID>();
        
        // Get the list of equivalences
        Subject s=getSubjectData(subject);            
        if(s!=null) {
            if(s.getEquivalentSubjects()!=null) {
                for(ISubjectID labID:s.getEquivalentSubjects().values()) {
                    eqList.add(labID);
                }
            }
        }
        
        // Create the output array
        ISubjectID[] retList=new ISubjectID[eqList.size()];
        eqList.toArray(retList);
        
        return retList;
    }
    
    public Subject getSubjectData(ISubjectID subjectID) throws AuthPelpException {
        
        assert(subjectID!=null);
        
        // Get main subject information
        Subject retObject=null;
        
        // Check authentication
        if(!isUserAuthenticated()) {
            throw new AuthPelpException("Authentication is reguired to retrieve this information");
        }
        
        // Obtain the subject information
        if(_dummySubjects.containsKey((SubjectID)subjectID)) {
            retObject=_dummySubjects.get((SubjectID)subjectID);
        }
        
        // Adapt the information to the user role
        if(isRole(UserRoles.MainTeacher,subjectID)) {
            // Coordinators will see all the available information
            return retObject;
        }                
        
        // Remove all the classrooms where user is not inscribed
        ArrayList<Classroom> classList=new ArrayList<Classroom>();
        if(retObject.getClassrooms()!=null) {
            for(Classroom classroom:retObject.getClassrooms().values()) {
                // Obtain filtered information for this classroom
                Classroom c=getClassroomData(classroom.getClassroomID());
                if(c!=null) {
                    // Add this classroom to the list of availabla classrooms
                    classList.add(c);
                }
            }
        }
        
        return retObject;
    }

    public Classroom getClassroomData(IClassroomID classroomID) throws AuthPelpException {

        assert(classroomID!=null);
        
        // Obtain the classroom data
        Classroom classroom=_dummySubjects.get(((ClassroomID)classroomID).getSubject()).getClassrooms().get(classroomID);
        
        // Remove non authorized information
        if(isRole(UserRoles.Teacher,classroom.getClassroomID())) {
            // Add this classroom with all the information
        } else if(isRole(UserRoles.Student,classroom.getClassroomID())) {                    
            // Store current user information
            Person user=classroom.getStudents().get(getUserID());

            // Remove the list of students
            classroom.clearStudents();

            // Add current user
            classroom.addStudent(user);
        } else {
            // Other users do not have access to this classroom
            classroom=null;
        }
        
        return classroom;
    }
    
    public Person getUserData() throws AuthPelpException {
        return getUserData(getUserID());
    }

    public Person getUserData(IUserID userID) throws AuthPelpException {
        
        assert(userID!=null);
        
        // A user can access to its information
        if(userID.equals(getUserID())) {
            return _dummyUsers.get((UserID)userID);
        }
        
        // Check if the current user can access to this user information.
        if(_dummySubjects!=null) {
            for(Subject s:_dummySubjects.values()) {               
                if((isRole(UserRoles.Teacher,s.getID()) && isRole(UserRoles.Student,s.getID(),userID)) || 
                (isRole(UserRoles.MainTeacher,s.getID()) && isRole(UserRoles.Student,s.getID(),userID)) ||
                (isRole(UserRoles.Student,s.getID()) && isRole(UserRoles.Teacher,s.getID(),userID)) || 
                (isRole(UserRoles.Student,s.getID()) && isRole(UserRoles.MainTeacher,s.getID(),userID))) {
                    return _dummyUsers.get((UserID)userID);
                }
            }
        }
        
        // If no access if allowed, return a null object
        return null;
    }
    
    private void createDummyData() {
        // Create dummy users with "punny" names (http://sandgroper14.wordpress.com/2007/04/30/fake-names-for-documentation/)
        _testAccessPersons=new Person[8];
        
        _testAccessPersons[0]=new Person(((IUserID)new UserID("000000")));
        _testAccessPersons[0]._eMail="user0@uoc.edu";
        _testAccessPersons[0]._fullName="Manny Jah";
        _testAccessPersons[0]._name="Manny";
        
        _testAccessPersons[1]=new Person(((IUserID)new UserID("111111")));
        _testAccessPersons[1]._eMail="user1@uoc.edu";
        _testAccessPersons[1]._fullName="Barb Akew";
        _testAccessPersons[1]._name="Barb";
        
        _testAccessPersons[2]=new Person(((IUserID)new UserID("222222")));
        _testAccessPersons[2]._eMail="user2@uoc.edu";
        _testAccessPersons[2]._fullName="Ann Chovey";
        _testAccessPersons[2]._name="Ann";
        
        _testAccessPersons[3]=new Person(((IUserID)new UserID("333333")));
        _testAccessPersons[3]._eMail="user3@uoc.edu";
        _testAccessPersons[3]._fullName="Hazel Nutt";
        _testAccessPersons[3]._name="Hazel";
        
        _testAccessPersons[4]=new Person(((IUserID)new UserID("444444")));
        _testAccessPersons[4]._eMail="user4@uoc.edu";
        _testAccessPersons[4]._fullName="Bess Twishes";
        _testAccessPersons[4]._name="Bess";
        
        _testAccessPersons[5]=new Person(((IUserID)new UserID("555555")));
        _testAccessPersons[5]._eMail="user5@uoc.edu";
        _testAccessPersons[5]._fullName="Chris Anthemum";
        _testAccessPersons[5]._name="Chris";
        
        _testAccessPersons[6]=new Person(((IUserID)new UserID("666666")));
        _testAccessPersons[6]._eMail="user6@uoc.edu";
        _testAccessPersons[6]._fullName="Harriet Upp";
        _testAccessPersons[6]._name="Harriet";
        
        _testAccessPersons[7]=new Person(((IUserID)new UserID("777777")));
        _testAccessPersons[7]._eMail="user7@uoc.edu";
        _testAccessPersons[7]._fullName="Theresa Green";
        _testAccessPersons[7]._name="Theresa";
        
        
        // Add users to the list of users
        _dummyUsers.clear();
        _dummyUsers.put((UserID)_testAccessPersons[0].getUserID(), _testAccessPersons[0]);
        _dummyUsers.put((UserID)_testAccessPersons[1].getUserID(), _testAccessPersons[1]);
        _dummyUsers.put((UserID)_testAccessPersons[2].getUserID(), _testAccessPersons[2]);
        _dummyUsers.put((UserID)_testAccessPersons[3].getUserID(), _testAccessPersons[3]);
        _dummyUsers.put((UserID)_testAccessPersons[4].getUserID(), _testAccessPersons[4]);
        _dummyUsers.put((UserID)_testAccessPersons[5].getUserID(), _testAccessPersons[5]);
        _dummyUsers.put((UserID)_testAccessPersons[6].getUserID(), _testAccessPersons[6]);
        _dummyUsers.put((UserID)_testAccessPersons[7].getUserID(), _testAccessPersons[7]);
        
        // Create the semesters
        try {    
            _dummySemesters.clear();
            _dummySemesters.put("20111",new Semester("20111",DateFormat.getDateInstance().parse("21/09/2011"),
                                                             DateFormat.getDateInstance().parse("15/01/2012")));
            _dummySemesters.put("20112",new Semester("20112",DateFormat.getDateInstance().parse("16/01/2011"),
                                                             DateFormat.getDateInstance().parse("01/08/2012")));
            _dummySemesters.put("20121",new Semester("20121",DateFormat.getDateInstance().parse("15/09/2012"),
                                                             DateFormat.getDateInstance().parse("17/01/2013")));
        } catch (ParseException ex) {
            Logger.getLogger(LocalCampusConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Create subjects
        _testAccessSubjects=new Subject[4];
        
        SubjectID sid1=new SubjectID("05.554",_dummySemesters.get("20111"));
        _testAccessSubjects[0]=new Subject(sid1);
        _testAccessSubjects[0].addMainTeacher(getTestPersonByPos(1));
        _testAccessSubjects[0].setDescription("Introduction to Java Programming");       
        _testAccessSubjects[0].setShortName("IJP");
              
        SubjectID sid2=new SubjectID("05.554",_dummySemesters.get("20112"));
        _testAccessSubjects[1]=new Subject(sid2);
        _testAccessSubjects[1].setDescription("Introduction to Java Programming"); 
        _testAccessSubjects[1].setShortName("IJP");
                
        SubjectID sid3=new SubjectID("05.564",_dummySemesters.get("20112"));
        _testAccessSubjects[2]=new Subject(sid3);
        _testAccessSubjects[2].setDescription("Introduction to C Programming");       
        _testAccessSubjects[2].setShortName("ICP");
        
        // Add the classrooms to created subjects
        Classroom cr1=new Classroom(new ClassroomID(sid1,1));
        cr1.addTeacher(getTestPersonByPos(2));
        cr1.addStudent(getTestPersonByPos(3));
        cr1.addStudent(getTestPersonByPos(4));
        Classroom cr2=new Classroom(new ClassroomID(sid1,2));
        cr2.addTeacher(getTestPersonByPos(5));
        cr2.addStudent(getTestPersonByPos(6));
        _testAccessSubjects[0].addClassroom(cr1);
        _testAccessSubjects[0].addClassroom(cr2);
        
                
        // Add the subjects to the list of subjects
        _dummySubjects.clear();
        _dummySubjects.put((SubjectID)_testAccessSubjects[0].getID(), _testAccessSubjects[0]);
        _dummySubjects.put((SubjectID)_testAccessSubjects[1].getID(), _testAccessSubjects[1]);
        _dummySubjects.put((SubjectID)_testAccessSubjects[2].getID(), _testAccessSubjects[2]);
    }
    
    /**
     * Obtains the subject information from the campus, without access restrictions.
     * @param subjectID Classroom identifier.
     * @return Object with all the information for given subject
     */
    private Subject getSubject(ISubjectID subjectID) {
        return _dummySubjects.get((SubjectID)subjectID);
    }
    
    /**
     * Obtains the classroom information from the campus, without access restrictions.
     * @param classroomID Classroom identifier.
     * @return Object with all the information for given classroom
     */
    private Classroom getClassroom(IClassroomID classroomID) {
        if(_dummySubjects!=null) {
            for(Subject s:_dummySubjects.values()) {
                if(s.getClassrooms()!=null) {
                    if(s.getClassrooms().containsKey(classroomID)) {
                        return s.getClassrooms().get(classroomID);
                    }
                }
            }
        }
        return null;
    }
    
    public void setProfile(String profileID) {
        // Each time that the profile changes, test data is generated
        createDummyData();
        
        // Depending on the description, select a certain user
        if("none".equalsIgnoreCase(profileID)) {
            // User not authenticated
            _userID=null;
        } else if("campus".equalsIgnoreCase(profileID)) {
            // Student with any subject
            _userID=(UserID) getTestPersonByPos(0).getUserID();
        } else if("student1".equalsIgnoreCase(profileID)) {
            // Student with an active subject with other students
            _userID=(UserID) getTestPersonByPos(3).getUserID();
        } else if("teacher1".equalsIgnoreCase(profileID)) {
            // Teacher with one student
            _userID=(UserID) getTestPersonByPos(5).getUserID();
        } else if("teacher2".equalsIgnoreCase(profileID)) {
            // Teacher with two students
            _userID=(UserID) getTestPersonByPos(2).getUserID();
        } else if("pra1".equalsIgnoreCase(profileID)) {
            // Main teacher whith a subject with two classrooms
            _userID=(UserID) getTestPersonByPos(1).getUserID();
        }
    }

    public Semester getTestSemester(String id) {
        return _dummySemesters.get(id);
    }

    public Subject getTestSubject(SubjectID id) {
        return _dummySubjects.get(id);
    }

    public Person getTestUser(UserID id) {
        return _dummyUsers.get(id);
    }

    public Subject getTestSubjectByPos(int pos) {
        return _testAccessSubjects[pos];
    }
    
    public Person getTestPersonByPos(int pos) {
        return _testAccessPersons[pos];
    }
    
    
}