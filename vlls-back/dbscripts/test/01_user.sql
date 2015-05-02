-- user --
INSERT INTO `user` (`name`, `password`, `email`, `first_name`, `last_name`, `point`, `birthday`, `bio`, `avatar`, `phone`, `fb_id`, `fb_access_token`, `gender`, `is_active`, `role_id`,`last_login`,`last_update`)
VALUES ('hiep', 'e152bad1ce99b787800ab5a1b97ef9d28d899ddf', 'hiep@hiep.com', 'Hiep', 'Ha', 0, NOW(), 'bio', 'assets/img/avatar/hiephn.png',
        '293847', NULL, NULL, 0, 1, 1, NOW(), NOW()),
  ('tuannka', 'e152bad1ce99b787800ab5a1b97ef9d28d899ddf', 'tuannkase60807@fpt.edu.vn', 'Tuan', 'Nguyen KA', 0, NOW(),
   'bio', 'assets/img/avatar/tuannka.png', '0965705296', NULL, NULL, 0, 1, 2, NOW(), NOW()),
  ('thongvh', 'e152bad1ce99b787800ab5a1b97ef9d28d899ddf', 'thongvhse60880@fpt.edu.vn', 'Thong', 'Thong VH', 0, NOW(),
    'bio', 'assets/img/avatar/thongvh.png', '0965705296', NULL, NULL, 0, 1, 2, NOW(), NOW()),
  ('phidh', 'e152bad1ce99b787800ab5a1b97ef9d28d899ddf', 'phidh60405@fpt.edu.vn', 'Phi', 'Dinh Hoang', 0, NOW(),
    'bio', 'assets/img/avatar/phidh.png', '0965705296', NULL, NULL, 0, 1, 2, NOW(), NOW()) ;
