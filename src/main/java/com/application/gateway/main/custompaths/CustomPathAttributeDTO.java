package com.application.gateway.main.custompaths;

import com.application.gateway.main.common.RequestInfoBase;
import com.application.gateway.main.custompaths.configuration.CustomPathsConfigurationDTO;
import com.application.gateway.main.custompaths.configuration.PathRules;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Represents custom path configuration to be used in CustomPathProvider.class.
 */
@Getter
@Setter
public class CustomPathAttributeDTO {

    /**
     * The custom path name.
     */
    private String name;

    /**
     * Represents the API ID to check permission according to KeyService.class.
     *
     * @see com.application.gateway.main.keymanager.KeyService#checkPermission(String, RequestInfoBase)
     */
    private String apiId;

    /**
     * Indicates whether to use basic authentication for the path.
     * Also, populates metadata if this value is true.
     */
    private boolean useBasicAuth;

    /**
     * The main path.
     */
    private String listenPath;

    /**
     * Performs middleware before handling the request.
     */
    private PathRules preMiddlewares;

    /**
     * Performs middleware after handling the request.
     */
    private PathRules postMiddlewares;

    /**
     * Indicates whether to use session and populate metadata if this value is true.
     */
    private Boolean useSession;

    /**
     * Applies policies according to the name.
     */
    private String policiesName;

    /**
     * Converts a CustomPathsConfigurationDTO object to a CustomPathAttributeDTO object.
     *
     * @param customPathsConfigurationDTO The CustomPathsConfigurationDTO to convert.
     * @return The converted CustomPathAttributeDTO object.
     */
    public static CustomPathAttributeDTO from(CustomPathsConfigurationDTO customPathsConfigurationDTO) {
        CustomPathAttributeDTO customPathAttributeDTO = new CustomPathAttributeDTO();
        customPathAttributeDTO.listenPath = customPathsConfigurationDTO.getListenPath();
        customPathAttributeDTO.apiId = customPathsConfigurationDTO.getApiId();
        customPathAttributeDTO.useBasicAuth = customPathsConfigurationDTO.isUseBasicAuth();
        customPathAttributeDTO.preMiddlewares = Objects.nonNull(customPathsConfigurationDTO.getPre()) ? customPathsConfigurationDTO.getPre() : null;
        customPathAttributeDTO.useSession = customPathsConfigurationDTO.getUseSession();
        customPathAttributeDTO.policiesName = customPathsConfigurationDTO.getPolicy();
        customPathAttributeDTO.postMiddlewares = Objects.nonNull(customPathsConfigurationDTO.getPost()) ? customPathsConfigurationDTO.getPost() : null;
        return customPathAttributeDTO;
    }
}
