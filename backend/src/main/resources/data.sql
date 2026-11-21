-- Clean out existing data to prevent duplicates on restart
DELETE FROM menu_item;

-- 1. KENYAN CUISINE
INSERT INTO menu_item (name, description, price, category, available, image_url) VALUES 
('Nyama Choma', 'Succulent goat meat slow-grilled over charcoal, served with ugali and kachumbari.', 1200.0, 'Kenyan', true, 'https://static.vecteezy.com/system/resources/previews/049/782/812/non_2x/tanzanian-nyama-choma-grilled-meat-served-with-ugali-and-kachumbari-fresh-tomato-and-onion-salad-placed-on-a-rustic-wooden-platter-with-bright-sunlight-photo.jpg'),
('Ugali & Sukuma Wiki', 'A staple Kenyan cornmeal cake served with sautéed collard greens and onions.', 450.0, 'Kenyan', true, 'https://upload.wikimedia.org/wikipedia/commons/4/48/Ugali_%26_Sukuma_Wiki.jpg');

-- 2. WESTERN CUISINE
INSERT INTO menu_item (name, description, price, category, available, image_url) VALUES 
('Gourmet Truffle Burger', 'Wagyu beef patty with black truffle aioli, wild mushrooms, and aged cheddar.', 1500.0, 'Western', true, 'https://www.foxvalleyfoodie.com/wp-content/uploads/2023/02/truffle-burger-recipe.jpg'),
('Creamy Alfredo Pasta', 'Fettuccine tossed in a rich parmesan cream sauce with garlic and fresh parsley.', 1100.0, 'Western', true, 'https://www.twistandmake.com/wp-content/uploads/2021/04/Creamy-Alfredo-Pasta-1.jpg');

-- 3. TANZANIAN CUISINE
INSERT INTO menu_item (name, description, price, category, available, image_url) VALUES 
('Chips Mayai', 'A beloved Tanzanian street food omelette made with fresh potato fries.', 550.0, 'Tanzanian', true, 'https://mayuris-jikoni.com/wp-content/uploads/2019/12/Chips-Mayai-1.jpg'),
('Zanzibari Pilau', 'Fragrant spiced rice cooked with beef, cloves, cardamom, and cinnamon.', 950.0, 'Tanzanian', true, 'https://images.food52.com/V9X_q-Y_UuN3Y-J0J6-J-J-J-J=/1200x675/0e9e1e3e-4d4e-4e4e-8e4e-0e9e1e3e4d4e.jpg');

-- 4. SOUTH SUDANESE CUISINE
INSERT INTO menu_item (name, description, price, category, available, image_url) VALUES 
('Kisra & Mulah', 'Thin sorghum pancakes served with a savory okra and meat stew.', 850.0, 'S. Sudanese', true, 'https://lookaside.fbsbx.com/lookaside/crawler/media/?media_id=10158482052445678'),
('Awalwala (Walwal)', 'Nutritious and comforting traditional thick millet porridge.', 400.0, 'S. Sudanese', true, 'https://lookaside.fbsbx.com/lookaside/crawler/media/?media_id=125891438758093');
