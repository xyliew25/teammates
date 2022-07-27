package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;

/**
 * Action: resets all accounts of logged-in user.
 */
class ResetOwnAccountsAction extends Action {

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
        if (userInfo.getIsStudent()) {
            List<StudentAttributes> students = logic.getStudentsForGoogleId(userInfo.id);
            for (StudentAttributes student : students) {
                try {
                    logic.resetStudentGoogleId(student.getEmail(), student.getCourse());
                    taskQueuer.scheduleCourseRegistrationInviteToStudent(student.getId(), student.getEmail(), true);
                } catch (EntityDoesNotExistException e) {
                    throw new EntityNotFoundException(e);
                }
            }
        }

        if (userInfo.getIsInstructor()) {
            List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(userInfo.id);
            for (InstructorAttributes instructor : instructors) {
                try {
                    logic.resetInstructorGoogleId(instructor.getEmail(), instructor.getCourseId());
                    taskQueuer.scheduleCourseRegistrationInviteToInstructor(null, instructor.getEmail(),
                            instructor.getCourseId(), true);
                } catch (EntityDoesNotExistException e) {
                    throw new EntityNotFoundException(e);
                }
            }
        }

        if (fileStorage.doesFileExist(userInfo.id)) {
            fileStorage.delete(userInfo.id);
        }
        logic.deleteAccountCascade(userInfo.id);

        return new JsonResult("Own accounts are successfully reset.");
    }

}
