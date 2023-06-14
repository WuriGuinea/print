package io.mosip.print.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The persistent class Processed RegPrc print List database table.
 *
 * @author Thamaraikannan
 * @since 1.0.0
 */

@Component
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "print_transaction", schema = "print")
public class PrintTranactionEntity implements Serializable {
    /**
     * The Print Id.
     */
    @Id
    @Column(name = "print_id")
    private String printId;

    /**
     * The Credential Transaction Id.
     */
    @Column(name = "credential_transaction_id")
    private String credentialTransactionId;

    /**
     * The Reference Id.
     */
    @Column(name = "reg_id")
    private String referenceId;

    /**
     * The status code.
     */
    @Column(name = "status_code")
    private String statusCode;

    /**
     * The status comment.
     */
    @Column(name = "status_comment")
    private String statusComment;
    /**
     * The Language code.
     */
    @Column(name = "lang_code")
    private String langCode;

    /**
     * The read time.
     */
    @Column(name = "read_dtimes")
    private LocalDateTime readDate;

    /**
     * The print time.
     */
    @Column(name = "print_dtimes")
    private LocalDateTime printDate;

    /**
     * The created by
     */
    @Column(name = "cr_by")
    private String crBy;

    /**
     * The created time.
     */
    @Column(name = "cr_dtimes")
    private LocalDateTime crDate;

    /**
     * The updated by.
     */
    @Column(name = "upd_by")
    private String upBy;

    /**
     * The updated time.
     */
    @Column(name = "upd_dtimes")
    private LocalDateTime updDate;

    /**
     * The is deleted.
     */
    @Column(name = "is_deleted")
    private boolean isDeleted;

    /**
     * The deleted time.
     */
    @Column(name = "del_dtimes")
    private LocalDateTime delTime;
}
