package net.petcu.store.web.rest;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import net.petcu.store.domain.PricedProduct;
import net.petcu.store.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
@EnableMethodSecurity
class AdminControllerTest {

    @Autowired
    private MockMvc restMockMvc;

    @MockBean
    private AdminService adminService;

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Double DEFAULT_NEW_PRICE = 15.0;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void GivenValidRequest_WhenChangePrice_ShouldReturnUpdatedPricedProduct() throws Exception {
        // Arrange
        PricedProduct expectedPricedProduct = new PricedProduct();
        when(adminService.changePrice(DEFAULT_PRODUCT_ID, DEFAULT_NEW_PRICE)).thenReturn(expectedPricedProduct);

        // Act & Assert
        restMockMvc
            .perform(
                put("/api/admin/products/{productId}/price", DEFAULT_PRODUCT_ID)
                    .param("newPrice", DEFAULT_NEW_PRICE.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(expectedPricedProduct.getId()));

        verify(adminService).changePrice(DEFAULT_PRODUCT_ID, DEFAULT_NEW_PRICE);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void GivenNonAdminUser_WhenChangePrice_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        restMockMvc
            .perform(
                put("/api/admin/products/{productId}/price", DEFAULT_PRODUCT_ID)
                    .param("newPrice", DEFAULT_NEW_PRICE.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andExpect(status().isForbidden());

        verify(adminService, never()).changePrice(any(), any());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void GivenInvalidPrice_WhenChangePrice_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        restMockMvc
            .perform(
                put("/api/admin/products/{productId}/price", DEFAULT_PRODUCT_ID)
                    .param("newPrice", "-1.0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andExpect(status().isBadRequest());

        verify(adminService, never()).changePrice(any(), any());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void GivenZeroPrice_WhenChangePrice_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        restMockMvc
            .perform(
                put("/api/admin/products/{productId}/price", DEFAULT_PRODUCT_ID)
                    .param("newPrice", "0.0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andExpect(status().isBadRequest());

        verify(adminService, never()).changePrice(any(), any());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void GivenNullPrice_WhenChangePrice_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        restMockMvc
            .perform(
                put("/api/admin/products/{productId}/price", DEFAULT_PRODUCT_ID)
                    .param("newPrice", "")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andExpect(status().isBadRequest());

        verify(adminService, never()).changePrice(any(), any());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void GivenMissingCsrfToken_WhenChangePrice_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        restMockMvc
            .perform(
                put("/api/admin/products/{productId}/price", DEFAULT_PRODUCT_ID)
                    .param("newPrice", DEFAULT_NEW_PRICE.toString())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isForbidden());

        verify(adminService, never()).changePrice(any(), any());
    }

    @Test
    @WithAnonymousUser
    void GivenUnauthenticatedUser_WhenChangePrice_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        restMockMvc
            .perform(
                put("/api/admin/products/{productId}/price", DEFAULT_PRODUCT_ID)
                    .param("newPrice", DEFAULT_NEW_PRICE.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
            )
            .andExpect(status().isUnauthorized());

        verify(adminService, never()).changePrice(any(), any());
    }
}
