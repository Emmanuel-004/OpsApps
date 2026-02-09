package com.dansmultipro.opsapps.specification;

import com.dansmultipro.opsapps.model.TransactionHistory;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TransactionHistorySpecification {

    public static Specification<TransactionHistory> filterByRoleAndUser(String roleCode, UUID userId) {
        return (root, query, cb) -> {
            switch (roleCode) {
                case "SA":
                    return cb.conjunction();

                case "PG":
                    Join<?, ?> transaction = root.join("transaction");
                    Join<?, ?> paymentGateway = transaction.join("paymentGateway");
                    Join<?, ?> paymentGatewayAdmins = paymentGateway.join("paymentGateawayAdmins");
                    return cb.equal(paymentGatewayAdmins.get("gateawayAdmin").get("id"), userId);

                case "CUS":
                    Join<?, ?> transaction2 = root.join("transaction");
                    Join<?, ?> customer = transaction2.join("customer");
                    return cb.equal(customer.get("id"), userId);

                default:
                    return cb.disjunction();
            }
        };
    }

    public static Specification<TransactionHistory> orderByCreatedDate() {
        return (root, query, cb) -> {
            if (query != null) {
                query.orderBy(cb.desc(root.get("createdAt")));
            }
            return null;
        };
    }
}
