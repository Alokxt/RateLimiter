local key = KEYS[1]
local now = tonumber(ARGV[1])
local maxRequests = tonumber(ARGV[2])
local windowSeconds = tonumber(ARGV[3])
local member = ARGV[4]  -- unique id for this request, generated in Java

local cutoff = now - windowSeconds

redis.call("ZREMRANGEBYSCORE", key, "-inf", cutoff)

local count = redis.call("ZCARD", key)

local allowed = 0
if count < maxRequests then
    redis.call("ZADD", key, now, member)
    allowed = 1
    count = count + 1
end

redis.call("EXPIRE", key, windowSeconds * 2)

return {allowed, tostring(maxRequests - count)}