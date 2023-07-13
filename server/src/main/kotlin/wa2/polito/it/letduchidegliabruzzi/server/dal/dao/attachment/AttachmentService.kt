package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment

interface AttachmentService {
    fun addAttachment(messageID: Int, fileBase64 :String) :Attachment
    fun getAttachmentsByMessageID(messageID: Int): List<AttachmentDTO>?
}