package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

/**
 * Action: resets an account ID.
 */
class UpdateAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        if (studentEmail == null && instructorEmail == null) {
            throw new InvalidHttpParameterException("Either student email or instructor email has to be specified.");
        }

        String newGoogleId = getRequestParamValue("newgoogleid");
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (studentEmail != null) {
            StudentAttributes existingStudent = logic.getStudentForEmail(courseId, studentEmail);
            if (existingStudent == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }

            try {
                logic.updateStudentGoogleId(studentEmail, courseId, newGoogleId);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        } else if (instructorEmail != null) {
            InstructorAttributes existingInstructor = logic.getInstructorForEmail(courseId, instructorEmail);
            if (existingInstructor == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }

            try {
                logic.updateInstructorGoogleId(instructorEmail, courseId, newGoogleId);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        }

        return new JsonResult("Account is successfully updated.");
    }

}
