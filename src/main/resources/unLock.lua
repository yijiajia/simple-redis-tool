-- 加锁步骤：通过 SET key value NX PX 即可
-- 1. 获取锁
-- 2. 判断锁是否存在；
-- 3. 设置锁
-- 解锁步骤：
-- 1. 获取锁；
-- 2. 判断锁变量是否是本地变量；
-- 3. 释放锁，即删除key;
if redis.call("get",KEYS[1]) == ARGV[1] then
    return redis.call("del",KEYS[1])
else
    return 0
end