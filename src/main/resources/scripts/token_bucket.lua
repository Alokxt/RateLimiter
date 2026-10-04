local key = KEYS[1]
local cap = tonumber(ARGV[1])
local windowSeconds = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local bucket = redis.call('HMGET', key, 'tokens', 'lastrefill')
local tokens = tonumber(bucket[1])
local lastRefill = tonumber(bucket[2])

if tokens == nil then
    tokens = cap
    lastRefill = now
end

local elapsed = now - lastRefill
local refillRate = cap / windowSeconds
local toAdd = elapsed * refillRate
tokens = math.min(toAdd + tokens, cap)

local allowed = 0
if tokens >=1  then
    allowed = 1
    tokens = tokens - 1
end

redis.call('HMSET', key, 'tokens', tokens, 'lastrefill', now)
redis.call('EXPIRE', key, windowSeconds * 2)

return {allowed, tostring(tokens)}