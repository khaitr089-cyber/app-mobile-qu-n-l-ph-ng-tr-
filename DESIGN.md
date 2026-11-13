# Hệ thống giao diện – Quản lý phòng trọ

Mọi màn hình dùng chung một bộ token + component. Khi thêm màn hình mới, **chỉ dùng các thứ dưới đây**, không đặt màu/cỡ chữ/bo góc thủ công.

## Màu (`values/colors.xml`, bản tối ở `values-night/colors.xml`)
| Token | Dùng cho |
|---|---|
| `?attr/colorPrimary` (teal đậm) | hành động chính, số tiền, icon, thẻ nổi bật |
| `?attr/colorPrimaryContainer` | nền icon, ô tổng tiền, nút phụ (tonal) |
| `@color/badge_vacant` (đồng thau) | phòng **còn trống** – cần chú ý |
| `@color/badge_occupied` (teal) | phòng **đang thuê** |
| `?attr/colorError` | xóa / đăng xuất |
| `?android:attr/colorBackground` | nền trang |
| `?attr/colorSurface` | nền card, ô nhập |
| `?attr/colorOnSurface` / `?attr/colorOnSurfaceVariant` | chữ chính / chữ phụ |
| `?attr/colorOutlineVariant` | viền card, đường kẻ |

## Chữ
| Style | Dùng cho |
|---|---|
| `App.Text.ScreenTitle` | tên màn hình (22sp) |
| `App.Text.SectionTitle` | tiêu đề card / danh sách (16sp) |
| `App.Text.Row` | nội dung thường (14–15sp) |
| `App.Text.Caption` | chú thích, thông tin phụ (13sp) |
| `App.Text.Amount` | số tiền (primary, đậm vừa) |
| `TextAppearance.App.Metric` | số liệu lớn trong thẻ nổi bật |

Chữ dùng **sentence case** (không IN HOA toàn bộ).

## Hình khối & khoảng cách
- Bo góc: ô nhập / nút 14dp · card 20dp · thẻ nổi bật 24dp · ô tìm kiếm dạng viên thuốc.
- Lề trang 16dp · khoảng cách giữa ô nhập 12dp · giữa card 16dp · khoảng cách ngang giữa 2 ô 8dp.
- Card **không đổ bóng**, chỉ có viền mảnh `colorOutlineVariant`.

## Nút (mức ưu tiên)
| Style | Ý nghĩa |
|---|---|
| `Widget.App.Button` | hành động chính (Thêm, Lưu, Đăng nhập) – tối đa 1 nút/khối |
| `Widget.App.Button.Tonal` | hành động phụ (Sửa) |
| `Widget.App.Button.Outlined` | trung tính (Làm mới, Đăng ký) |
| `Widget.App.Button.Danger` | phá hủy (Xóa, Đăng xuất) |
| `Widget.App.Button.Text` | liên kết (Đã có tài khoản?) |
| `Widget.App.Button.Back` | nút quay lại tròn ở đầu mỗi màn hình |

## Khung một màn hình con
`LinearLayout (fitsSystemWindows)` → thanh tiêu đề (nút quay lại + tên) → `ScrollView` → các `MaterialCardView`.
Danh sách dùng `RecyclerView` `wrap_content` + item là `MaterialCardView` (xem `item_phong.xml`).
