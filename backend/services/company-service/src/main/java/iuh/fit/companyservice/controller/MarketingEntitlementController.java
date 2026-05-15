package iuh.fit.companyservice.controller;

import iuh.fit.companyservice.Service.MarketingEntitlementService;
import iuh.fit.companyservice.dto.request.CompanyMarketingAssignmentRequest;
import iuh.fit.companyservice.dto.response.CompanyMarketingAssignmentResponse;
import iuh.fit.companyservice.dto.response.CompanyMarketingEntitlementResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company/marketing-entitlements")
public class MarketingEntitlementController {
    private final MarketingEntitlementService marketingEntitlementService;

    public MarketingEntitlementController(MarketingEntitlementService marketingEntitlementService) {
        this.marketingEntitlementService = marketingEntitlementService;
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CompanyMarketingEntitlementResponse>> getByCompany(
            @PathVariable("companyId") String companyId,
            @RequestParam(value = "category", required = false) String category
    ) {
        return ResponseEntity.ok(marketingEntitlementService.getByCompanyId(companyId, category));
    }

    @GetMapping("/company/{companyId}/assignments")
    public ResponseEntity<List<CompanyMarketingAssignmentResponse>> getAssignments(
            @PathVariable("companyId") String companyId,
            @RequestParam(value = "targetScope", required = false) String targetScope,
            @RequestParam(value = "targetId", required = false) String targetId
    ) {
        return ResponseEntity.ok(marketingEntitlementService.getAssignments(companyId, targetScope, targetId));
    }

    @GetMapping("/company/{companyId}/active-assignment")
    public ResponseEntity<CompanyMarketingAssignmentResponse> getActiveAssignment(
            @PathVariable("companyId") String companyId,
            @RequestParam("targetScope") String targetScope,
            @RequestParam("targetId") String targetId
    ) {
        return ResponseEntity.ok(marketingEntitlementService.getActiveAssignmentForTarget(companyId, targetScope, targetId));
    }

    @PostMapping("/{entitlementId}/assign")
    public ResponseEntity<CompanyMarketingAssignmentResponse> assignEntitlement(
            @PathVariable("entitlementId") String entitlementId,
            @RequestBody CompanyMarketingAssignmentRequest request
    ) {
        return ResponseEntity.ok(marketingEntitlementService.assignEntitlement(entitlementId, request));
    }

    @DeleteMapping("/assignments/{assignmentId}")
    public ResponseEntity<CompanyMarketingAssignmentResponse> removeAssignment(
            @PathVariable("assignmentId") String assignmentId,
            @RequestParam("companyId") String companyId
    ) {
        return ResponseEntity.ok(marketingEntitlementService.removeAssignment(assignmentId, companyId));
    }

    @PostMapping("/expire")
    public ResponseEntity<List<String>> expireEntitlements() {
        return ResponseEntity.ok(marketingEntitlementService.expireEntitlements());
    }
}
