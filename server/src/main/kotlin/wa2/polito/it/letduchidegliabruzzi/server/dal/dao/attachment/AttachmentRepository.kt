package wa2.polito.it.letduchidegliabruzzi.server.dal.dao.attachment

import org.springframework.data.jpa.repository.JpaRepository

interface AttachmentRepository:JpaRepository<Attachment, Int> {
}