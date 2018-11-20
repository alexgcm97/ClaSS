/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import da.CourseDA;
import da.ProgrammeCohortDA;
import da.StaffDA;
import da.TutorialGroupDA;
import domain.Course;
import domain.CourseDetails;
import domain.ProgrammeCohort;
import domain.Staff;
import domain.TutorialGroup;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author DEREK
 */
@ManagedBean
@SessionScoped
public class staffManage implements Serializable {

    public TutorialGroupDA tgda = new TutorialGroupDA();
    public ProgrammeCohortDA pcda = new ProgrammeCohortDA();
    public ProgrammeCohort pc = new ProgrammeCohort();
    public StaffDA sda = new StaffDA();
    public CourseDA cda = new CourseDA();
    public CourseDetails cd = new CourseDetails();
    public Course c = new Course();
    public Staff s = new Staff();
    public TutorialGroup tg = new TutorialGroup();
    private List<String> selectedCourseCodeList = new ArrayList();
    private List<String> courseCodeAndType = new ArrayList();
    private List<String> courseCodeStr = new ArrayList();
    private List<String> courseCodeString = new ArrayList();
    private String[] lecGroupStr, tutGroupStr, pracGroupStr;
    private String lectureListString, tutorialListString, practicalListString, courseListString;
    private List<String> selectedTutGroupList = new ArrayList();
    private List<String> selectedPracGroupList = new ArrayList();
    private List<ProgrammeCohort> pcList = new ArrayList();
    private List<TutorialGroup> tutorialGroupList = new ArrayList();
    private List<TutorialGroup> practicalGroupList = new ArrayList();
    private int blockStart = 0;

    public TutorialGroup getTg() {
        return tg;
    }

    public String getLectureListString() {
        return lectureListString;
    }

    public void setLectureListString(String lectureListString) {
        this.lectureListString = lectureListString;
    }

    public String getTutorialListString() {
        return tutorialListString;
    }

    public List<String> getCourseCodeString() {
        return courseCodeString;
    }

    public void setCourseCodeString(List<String> courseCodeString) {
        this.courseCodeString = courseCodeString;
    }

    public void setTutorialListString(String tutorialListString) {
        this.tutorialListString = tutorialListString;
    }

    public String getPracticalListString() {
        return practicalListString;
    }

    public void setPracticalListString(String practicalListString) {
        this.practicalListString = practicalListString;
    }

    public List<String> getSelectedTutGroupList() {
        return selectedTutGroupList;
    }

    public List<TutorialGroup> getTutorialGroupList() {
        return tutorialGroupList;
    }

    public void setTutorialGroupList(List<TutorialGroup> tutorialGroupList) {
        this.tutorialGroupList = tutorialGroupList;
    }

    public void setSelectedTutGroupList(List<String> selectedTutGroupList) {
        this.selectedTutGroupList = selectedTutGroupList;
    }

    public List<String> getSelectedPracGroupList() {
        return selectedPracGroupList;
    }

    public void setSelectedPracGroupList(List<String> selectedPracGroupList) {
        this.selectedPracGroupList = selectedPracGroupList;
    }

    public String getCourseListString() {
        return courseListString;
    }

    public void setCourseListString(String courseListString) {
        this.courseListString = courseListString;
    }

    public void setTg(TutorialGroup tg) {
        this.tg = tg;
    }

    public int getBlockStart() {
        return blockStart;
    }

    public CourseDetails getCd() {
        return cd;
    }

    public void setCd(CourseDetails cd) {
        this.cd = cd;
    }

    public Course getC() {
        return c;
    }

    public void setC(Course c) {
        this.c = c;
    }

    public List<ProgrammeCohort> getPcList() {
        return pcList;
    }

    public void setPcList(List<ProgrammeCohort> pcList) {
        this.pcList = pcList;
    }

    public List<String> getCourseCodeAndType() {
        return courseCodeAndType;
    }

    public void setCourseCodeAndType(List<String> courseCodeAndType) {
        this.courseCodeAndType = courseCodeAndType;
    }

    public List<String> getCourseCodeStr() {
        return courseCodeStr;
    }

    public void setCourseCodeStr(List<String> courseCodeStr) {
        this.courseCodeStr = courseCodeStr;
    }

    public List<String> getSelectedCourseCodeList() {
        return selectedCourseCodeList;
    }

    public void setSelectedCourseCodeList(List<String> selectedCourseCodeList) {
        this.selectedCourseCodeList = selectedCourseCodeList;
    }

    public String[] getLecGroupStr() {
        return lecGroupStr;
    }

    public void setLecGroupStr(String[] lecGroupStr) {
        this.lecGroupStr = lecGroupStr;
    }

    public String[] getTutGroupStr() {
        return tutGroupStr;
    }

    public void setTutGroupStr(String[] tutGroupStr) {
        this.tutGroupStr = tutGroupStr;
    }

    public String[] getPracGroupStr() {
        return pracGroupStr;
    }

    public void setPracGroupStr(String[] pracGroupStr) {
        this.pracGroupStr = pracGroupStr;
    }

    public ProgrammeCohort getPc() {
        return pc;
    }

    public void setPc(ProgrammeCohort pc) {
        this.pc = pc;
    }

    public void setBlockStart(int blockStart) {
        this.blockStart = blockStart;
    }

    public List<TutorialGroup> getPracticalGroupList() {
        return practicalGroupList;
    }

    public void setPracticalGroupList(List<TutorialGroup> practicalGroupList) {
        this.practicalGroupList = practicalGroupList;
    }

    public Staff getS() {
        return s;
    }

    public void setS(Staff s) {
        this.s = s;
    }

    public StaffDA getSda() {
        return sda;
    }

    public void setSda(StaffDA sda) {
        this.sda = sda;
    }

    public void goTonew() throws IOException {

        s.setBlockStart(0);
        FacesContext.getCurrentInstance().getExternalContext().redirect("newStaff.xhtml");

    }

    public void check() throws SQLException {
        System.out.println("--------------COURSECODE HERE!!--------------------");
        int foundCourse = 0;
        for (String str : courseCodeStr) {
            boolean found = false;
            String temp[] = str.split("-");
            for (int i = 0; i < courseCodeString.size(); i++) {
                if (courseCodeString.get(i).contains(temp[0])) {
                    found = true;
                    foundCourse = i;
                    break;
                }
            }
            if (found) {
                String result = courseCodeString.get(foundCourse);
                result += "," + temp[1];
                courseCodeString.set(foundCourse, result);

            } else {
                courseCodeString.add(str);
            }
        }

        courseListString = null;
        String substringCourse = "";
        if (!courseCodeString.isEmpty()) {
            for (String str : courseCodeString) {
                System.out.println(str);
                substringCourse += str;
                substringCourse += '|';

            }
            courseListString = substringCourse.substring(0, substringCourse.length() - 1);
            System.out.println(courseListString);
        } else {
            System.out.println("COURSE EMPTY");
        }

        System.out.println("--------------END OF COURSECODE-----------------------");
        System.out.println("--------------LEC HERE!!--------------------");
        int foundIndex = 0;
        s.setLecGroupList(new ArrayList<String>());
        for (String str : lecGroupStr) {
            boolean found = false;
            String temp[] = str.split("-");
            for (int i = 0; i < s.getLecGroupList().size(); i++) {
                if (s.getLecGroupList().get(i).contains(temp[0])) {
                    found = true;
                    foundIndex = i;
                    break;
                }
            }
            if (found) {
                String result = s.getLecGroupList().get(foundIndex);
                result += "," + temp[1];
                s.getLecGroupList().set(foundIndex, result);
            } else {
                s.getLecGroupList().add(str);
            }
        }
        lectureListString = null;
        String substring = "";
        if (!s.getLecGroupList().isEmpty()) {
            for (String str : s.getLecGroupList()) {
                System.out.println(str);
                substring += str;
                substring += '|';

            }
            lectureListString = substring.substring(0, substring.length() - 1);
            System.out.println(lectureListString);
        } else {
            System.out.println("LECTURE EMPTY");
        }
        System.out.println("--------------END OF LEC-----------------------");

        System.out.println("--------------TUT HERE!!--------------------");
        int foundIndex1 = 0;
        s.setTutGroupList(new ArrayList<String>());
        for (String str1 : tutGroupStr) {
            boolean found1 = false;
            String temp[] = str1.split("-");
            for (int i = 0; i < s.getTutGroupList().size(); i++) {
                if (s.getTutGroupList().get(i).contains(temp[0])) {
                    found1 = true;
                    foundIndex1 = i;
                    break;
                }
            }
            if (found1) {
                String result = s.getTutGroupList().get(foundIndex1);
                result += "," + temp[1];
                s.getTutGroupList().set(foundIndex1, result);
            } else {
                s.getTutGroupList().add(str1);
            }
        }
        tutorialListString = null;
        String substring1 = "";
        if (!s.getTutGroupList().isEmpty()) {
            for (String str : s.getTutGroupList()) {
                System.out.println(str);
                substring1 += str;
                substring1 += '|';

            }
            tutorialListString = substring1.substring(0, substring1.length() - 1);
            System.out.println(tutorialListString);
        } else {
            System.out.println("TUTORIAL EMPTY");
        }
        System.out.println("--------------END OF TUT-----------------------");

        System.out.println("--------------PRAC HERE!!--------------------");
        int foundIndex2 = 0;
        s.setPracGroupList(new ArrayList<String>());
        for (String str1 : pracGroupStr) {
            boolean found1 = false;
            String temp[] = str1.split("-");
            for (int i = 0; i < s.getPracGroupList().size(); i++) {
                if (s.getPracGroupList().get(i).contains(temp[0])) {
                    found1 = true;
                    foundIndex2 = i;
                    break;
                }
            }
            if (found1) {
                String result = s.getPracGroupList().get(foundIndex2);
                result += "," + temp[1];
                s.getPracGroupList().set(foundIndex2, result);
            } else {
                s.getPracGroupList().add(str1);
            }
        }
        practicalListString = null;
        String substring2 = "";
        if (!s.getPracGroupList().isEmpty()) {
            for (String str : s.getPracGroupList()) {
                System.out.println(str);
                substring2 += str;
                substring2 += '|';

            }
            practicalListString = substring2.substring(0, substring2.length() - 1);
            System.out.println(practicalListString);
        } else {
            System.out.println("PRACTICAL EMPTY");
        }
        System.out.println("--------------END OF PRAC-----------------------");

    }

    public void staffInsert() throws SQLException, IOException {
        s.setBlockStart(blockStart);
        check();
        if (courseListString == null) {
            s.setCourseCodeListS("-");

        } else {
            s.setCourseCodeListS(courseListString);
        }
        if (lectureListString == null) {
            s.setLecGroupListS("-");

        } else {
            s.setLecGroupListS(lectureListString);
        }
        if (tutorialListString == null) {
            s.setTutGroupListS("-");

        } else {
            s.setTutGroupListS(tutorialListString);
        }
        if (practicalListString == null) {
            s.setPracGroupListS("-");
        } else {
            s.setPracGroupListS(practicalListString);
        }

        sda.insertStaff(s);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectStaff.xhtml");
    }

    public List<ProgrammeCohort> getLecGroupListMethod() throws SQLException {
        List<String> courseCodeStrTrimmed = new ArrayList<String>();
        System.out.println("getLecGroupList");
        System.out.println("selectedCourse:" + courseCodeStr.size());

        for (int i = 0; i < courseCodeStr.size(); i++) {
            System.out.println(courseCodeStr.get(i));
            if (!courseCodeStrTrimmed.contains(courseCodeStr.get(i).substring(0, courseCodeStr.get(i).length() - 2))) {
                courseCodeStrTrimmed.add(courseCodeStr.get(i).substring(0, courseCodeStr.get(i).length() - 2));
            }
        }
        System.out.println("trimmed size:" + courseCodeStrTrimmed.size());
        for (int a = 0; a < courseCodeStrTrimmed.size(); a++) {
            System.out.println(courseCodeStrTrimmed.get(a));
        }

        List<ProgrammeCohort> pcresult = new ArrayList<ProgrammeCohort>();
        List<String> cohortIdList = new ArrayList<String>();
        pcList.clear();
        for (int b = 0; b < courseCodeStrTrimmed.size(); b++) {
            pcresult = pcda.getCourseCodePC(courseCodeStrTrimmed.get(b));
            System.out.println("pcResult size:" + pcresult.size());
            cohortIdList.clear();
            for (int c = 0; c < pcresult.size(); c++) {
                if (!cohortIdList.contains(pcresult.get(c).getCohortID())) {
                    cohortIdList.add(pcresult.get(c).getCohortID());
                }

            }
            System.out.println("cohortidlist size:" + cohortIdList.size());
            for (int j = 0; j < cohortIdList.size(); j++) {
                System.out.println(cohortIdList.get(j));
            }
            for (int i = 0; i < cohortIdList.size(); i++) {
                ProgrammeCohort pgListViaCohort = pcda.get(cohortIdList.get(i));
                pgListViaCohort.setCourseCodeList(courseCodeStrTrimmed.get(b));
                pcList.add(pgListViaCohort);
            }
        }

        return pcList;
    }

    public List<TutorialGroup> getPracGroupListMethod() throws SQLException {
        List<String> courseCodeStrTrimmed = new ArrayList<String>();
        System.out.println("getTutGroupList");
        System.out.println("TG selectedCourse:" + courseCodeStr.size());

        for (int i = 0; i < courseCodeStr.size(); i++) {
            System.out.println(courseCodeStr.get(i));
            if (!courseCodeStrTrimmed.contains(courseCodeStr.get(i).substring(0, courseCodeStr.get(i).length() - 2))) {
                courseCodeStrTrimmed.add(courseCodeStr.get(i).substring(0, courseCodeStr.get(i).length() - 2));
            }
        }
        System.out.println("TG trimmed size:" + courseCodeStrTrimmed.size());
        for (int a = 0; a < courseCodeStrTrimmed.size(); a++) {
            System.out.println(courseCodeStrTrimmed.get(a));
        }
        List<TutorialGroup> tgresult1 = new ArrayList<TutorialGroup>();
        List<TutorialGroup> tgresult = new ArrayList<TutorialGroup>();
        List<ProgrammeCohort> pcresult = new ArrayList<ProgrammeCohort>();
        List<String> cohortIdList = new ArrayList<String>();
        List<String> tgList = new ArrayList<String>();
        practicalGroupList.clear();
        for (int b = 0; b < courseCodeStrTrimmed.size(); b++) {
            pcresult = pcda.getCourseCodePC(courseCodeStrTrimmed.get(b));
            System.out.println("TG pcResult size:" + pcresult.size());
            cohortIdList.clear();
            for (int c = 0; c < pcresult.size(); c++) {
                if (!cohortIdList.contains(pcresult.get(c).getCohortID())) {
                    cohortIdList.add(pcresult.get(c).getCohortID());
                }

            }
            System.out.println("TG cohortidlist size:" + cohortIdList.size());
            for (int j = 0; j < cohortIdList.size(); j++) {
                System.out.println(cohortIdList.get(j));
            }
            for (int i = 0; i < cohortIdList.size(); i++) {
                ProgrammeCohort pgListViaCohort = pcda.get(cohortIdList.get(i));
                tgresult = tgda.getTGviaCohortID(cohortIdList.get(i));
                System.out.println("TG result size:" + tgresult.size());
                for (int z = 0; z < tgresult.size(); z++) {
                    tgList.add(tgresult.get(z).getGroupID());
                    System.out.println(tgresult.get(z).getGroupID());
                    System.out.println("tgList size:" + tgList.size());
                    for (int k = 0; k < tgList.size(); k++) {
                        System.out.println(tgList.get(k));
                        tgresult1 = tgda.getSelectedRecords(tgList.get(k));
                    }
                    for (int w = 0; w < tgresult1.size(); w++) {
                        TutorialGroup tutorialG = new TutorialGroup();
                        tutorialG.setCohortID(tgresult1.get(w).getCohortID());
                        tutorialG.setEntryYear(tgresult1.get(w).getEntryYear());
                        tutorialG.setGroupID(tgresult1.get(w).getGroupID());
                        tutorialG.setGroupNumber(tgresult1.get(w).getGroupNumber());
                        tutorialG.setIntakeYear(tgresult1.get(w).getIntakeYear());
                        tutorialG.setProgrammeCode(tgresult1.get(w).getProgrammeCode());
                        tutorialG.setSize(tgresult1.get(w).getSize());
                        tutorialG.setStudyYear(tgresult1.get(w).getStudyYear());
                        tutorialG.setCourseCodeList(courseCodeStrTrimmed.get(b));
                        practicalGroupList.add(tutorialG);
                    }
                }
            }
        }

        return practicalGroupList;

    }

    public List<TutorialGroup> getTutGroupListMethod() throws SQLException {
        List<String> courseCodeStrTrimmed = new ArrayList<String>();
        System.out.println("getTutGroupList");
        System.out.println("TG selectedCourse:" + courseCodeStr.size());

        for (int i = 0; i < courseCodeStr.size(); i++) {
            System.out.println(courseCodeStr.get(i));
            if (!courseCodeStrTrimmed.contains(courseCodeStr.get(i).substring(0, courseCodeStr.get(i).length() - 2))) {
                courseCodeStrTrimmed.add(courseCodeStr.get(i).substring(0, courseCodeStr.get(i).length() - 2));
            }
        }
        System.out.println("TG trimmed size:" + courseCodeStrTrimmed.size());
        for (int a = 0; a < courseCodeStrTrimmed.size(); a++) {
            System.out.println(courseCodeStrTrimmed.get(a));
        }
        List<TutorialGroup> tgresult1 = new ArrayList<TutorialGroup>();
        List<TutorialGroup> tgresult = new ArrayList<TutorialGroup>();
        List<ProgrammeCohort> pcresult = new ArrayList<ProgrammeCohort>();
        List<String> cohortIdList = new ArrayList<String>();
        List<String> tgList = new ArrayList<String>();
        tutorialGroupList.clear();
        for (int b = 0; b < courseCodeStrTrimmed.size(); b++) {
            pcresult = pcda.getCourseCodePC(courseCodeStrTrimmed.get(b));
            System.out.println("TG pcResult size:" + pcresult.size());
            cohortIdList.clear();
            for (int c = 0; c < pcresult.size(); c++) {
                if (!cohortIdList.contains(pcresult.get(c).getCohortID())) {
                    cohortIdList.add(pcresult.get(c).getCohortID());
                }

            }
            System.out.println("TG cohortidlist size:" + cohortIdList.size());
            for (int j = 0; j < cohortIdList.size(); j++) {
                System.out.println(cohortIdList.get(j));
            }
            for (int i = 0; i < cohortIdList.size(); i++) {
                ProgrammeCohort pgListViaCohort = pcda.get(cohortIdList.get(i));
                tgresult = tgda.getTGviaCohortID(cohortIdList.get(i));
                System.out.println("TG result size:" + tgresult.size());
                for (int z = 0; z < tgresult.size(); z++) {
                    tgList.add(tgresult.get(z).getGroupID());
                    System.out.println(tgresult.get(z).getGroupID());
                    System.out.println("tgList size:" + tgList.size());
                    for (int k = 0; k < tgList.size(); k++) {
                        System.out.println(tgList.get(k));
                        tgresult1 = tgda.getSelectedRecords(tgList.get(k));
                    }
                    for (int w = 0; w < tgresult1.size(); w++) {
                        TutorialGroup tutorialG = new TutorialGroup();
                        tutorialG.setCohortID(tgresult1.get(w).getCohortID());
                        tutorialG.setEntryYear(tgresult1.get(w).getEntryYear());
                        tutorialG.setGroupID(tgresult1.get(w).getGroupID());
                        tutorialG.setGroupNumber(tgresult1.get(w).getGroupNumber());
                        tutorialG.setIntakeYear(tgresult1.get(w).getIntakeYear());
                        tutorialG.setProgrammeCode(tgresult1.get(w).getProgrammeCode());
                        tutorialG.setSize(tgresult1.get(w).getSize());
                        tutorialG.setStudyYear(tgresult1.get(w).getStudyYear());
                        tutorialG.setCourseCodeList(courseCodeStrTrimmed.get(b));
                        tutorialGroupList.add(tutorialG);
                    }
                }
            }
        }

        return tutorialGroupList;

    }

    public List<String> getCourseCodeListType() throws SQLException {
        List<Course> courseList = cda.getAllRecords();
        for (Course c : courseList) {
            this.selectedCourseCodeList.add(c.getCourseCode());
        }

        List<CourseDetails> courseDetailsList = cda.getRelatedCourseRecords(selectedCourseCodeList);
        for (CourseDetails cd : courseDetailsList) {
            if (cd.getLecHours() > 0) {
                courseCodeAndType.add(cd.getCourseCode() + "-" + "L");
            }

            if (cd.getTutHours() > 0) {
                courseCodeAndType.add(cd.getCourseCode() + "-" + "T");
            }

            if (cd.getPracHours() > 0) {
                courseCodeAndType.add(cd.getCourseCode() + "-" + "P");
            }
        }

        return this.courseCodeAndType;
    }

    public void retrieveStaff(String staffID) throws SQLException, IOException {
        s = sda.get(staffID);
        blockStart = (int) s.getBlockStart();
        FacesContext.getCurrentInstance().getExternalContext().redirect("editStaff.xhtml");

    }

    public void updateStaff() throws SQLException, IOException {
        s.setBlockStart(blockStart);
        sda.updateStaff(s);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectStaff.xhtml");
    }

    public void deleteStaff(String staffID) throws SQLException, IOException {
        sda.deleteStaff(staffID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectStaff.xhtml");
    }

    public void goBackMenu() throws IOException {
        sda.reset();
        FacesContext.getCurrentInstance().getExternalContext().redirect("EditInfo.xhtml");
    }

    public void goBackStaff() throws IOException {
        sda.reset();
        FacesContext.getCurrentInstance().getExternalContext().redirect("selectStaff.xhtml");
    }

    public void goToNew() throws IOException {
        s.setStaffName("");
        s.setBlockDay(0);
        s.setBlockDuration(0);
        s.setBlockStart(0);
        s.setCourseCodeListS("");
        s.setLecGroupListS("");
        s.setTutGroupListS("");
        s.setPracGroupListS("");
        FacesContext.getCurrentInstance().getExternalContext().redirect("newStaff.xhtml");
    }
}
