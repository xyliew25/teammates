package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

/**
 * Action: resets all accounts of logged-in user.
 */
class ResetOwnAccountAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isStudent || !userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Student or instructor account is required to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String wrongGoogleId = null;

        if (userInfo.isStudent) {
            StudentAttributes existingStudent = logic.getStudentForGoogleId(courseId, userInfo.getId());
            if (existingStudent == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }
            wrongGoogleId = existingStudent.getGoogleId();

            try {
                logic.resetStudentGoogleId(existingStudent.getEmail(), courseId);
                taskQueuer.scheduleCourseRegistrationInviteToStudent(
                        courseId, existingStudent.getEmail(), true);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        } else if (userInfo.isInstructor) {
            InstructorAttributes existingInstructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            if (existingInstructor == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }
            wrongGoogleId = existingInstructor.getGoogleId();

            try {
                logic.resetInstructorGoogleId(existingInstructor.getEmail(), courseId);
                taskQueuer.scheduleCourseRegistrationInviteToInstructor(
                        null, existingInstructor.getEmail(), courseId, true);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        }

        if (wrongGoogleId != null
                && logic.getStudentsForGoogleId(wrongGoogleId).isEmpty()
                && logic.getInstructorsForGoogleId(wrongGoogleId).isEmpty()) {
            if (fileStorage.doesFileExist(wrongGoogleId)) {
                fileStorage.delete(wrongGoogleId);
            }
            logic.deleteAccountCascade(wrongGoogleId);
        }

        return new JsonResult("Own account is successfully reset.");
    }

}
