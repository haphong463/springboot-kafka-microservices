package net.javaguides.identity_service.config;

import lombok.Getter;
import net.javaguides.identity_service.entity.Permission;
import net.javaguides.identity_service.entity.UserCredential;
import net.javaguides.identity_service.enums.EPermission;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private Set<String> permissions;

    public CustomUserDetails(Long id, String username, String password, String email, Collection<? extends GrantedAuthority> authorities, Set<String> permissions) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.permissions = permissions;
    }

    public CustomUserDetails(UserCredential userCredential) {
        this.username = userCredential.getName();
        this.password = userCredential.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Phương thức để trả về danh sách các authority dưới dạng mảng String
    public List<String> getRoleNames() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)  // Lấy tên của mỗi authority
                .collect(Collectors.toList());
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Phương thức build để tạo CustomUserDetails từ UserCredential
    public static CustomUserDetails build(UserCredential user) {
        // Lấy danh sách vai trò (roles) và chuyển thành danh sách SimpleGrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        // Lấy danh sách các quyền (permissions) và chuyển thành danh sách String
        Set<String> permissions = user.getPermissions().stream()
                .map(permission -> permission.getName().name())  // Lấy tên của quyền
                .collect(Collectors.toSet());

        return new CustomUserDetails(
                user.getId(),
                user.getName(),
                user.getPassword(),
                user.getEmail(),
                authorities,
                permissions // Có thể tùy chỉnh nếu muốn lưu trữ permissions dạng khác
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CustomUserDetails user = (CustomUserDetails) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
