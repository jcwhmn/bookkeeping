# MCP Server — Model Context Protocol for AI Agents

**Status**: ✅ Implemented  
**Date**: 2026-05-28

---

## Overview

MCP (Model Context Protocol) provides a simplified JSON-RPC style interface for AI agents to interact with the bookkeeping system. AI agents can use this interface to:

- Query transactions, accounts, categories
- Create, update, delete transactions
- Get statistics and insights
- Manage scheduled transactions

---

## API Endpoints

### Tool Discovery

```
GET /api/v1/mcp/tools
```

Returns list of available tools with their input schemas.

### Tool Execution

```
POST /api/v1/mcp/call
```

Execute a tool by name with parameters.

---

## Available Tools

| Tool | Description | Parameters |
|------|-------------|------------|
| `get_transactions` | Get transactions with filters | year, month, accountId, limit |
| `create_transaction` | Create new transaction | transactionType, accountId, amount, etc. |
| `get_accounts` | Get all accounts | - |
| `get_categories` | Get all categories | - |
| `get_statistics` | Get monthly statistics | year, month |
| `search_transactions` | Search by text | query, limit |
| `update_transaction` | Update existing transaction | id, transactionType, accountId, etc. |
| `delete_transaction` | Delete transaction | id |

---

## Usage Examples

### List Tools
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/mcp/tools
```

Response:
```json
{
  "success": true,
  "result": {
    "tools": [
      {
        "name": "get_transactions",
        "description": "Get transactions with optional filters",
        "inputSchema": { ... }
      },
      ...
    ]
  }
}
```

### Execute Tool
```bash
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"tool": "get_transactions", "params": {"year": 2026, "month": 5}}' \
  http://localhost:8080/api/v1/mcp/call
```

### Create Transaction
```bash
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tool": "create_transaction",
    "params": {
      "transactionType": 3,
      "accountId": 1,
      "amount": 5000,
      "description": "Grocery shopping"
    }
  }' \
  http://localhost:8080/api/v1/mcp/call
```

---

## Response Format

```json
{
  "success": true,
  "result": { ... },
  "error": null
}
```

On error:
```json
{
  "success": false,
  "result": null,
  "error": "Error message"
}
```

---

## AI Agent Integration

### Example: Claude Desktop Integration

Create a config file at `~/.claude/mcp.json`:

```json
{
  "mcpServers": {
    "bookkeeping": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-http", 
               "http://localhost:8080/api/v1/mcp"]
    }
  }
}
```

### Example: OpenAI Function Calling

Define tools in your OpenAI assistant:

```json
{
  "tools": [
    {
      "type": "function",
      "function": {
        "name": "get_transactions",
        "description": "Get bookkeeping transactions",
        "parameters": {
          "type": "object",
          "properties": {
            "year": { "type": "integer" },
            "month": { "type": "integer" }
          }
        }
      }
    }
  ]
}
```

---

## Security

- All endpoints require JWT authentication (`Authorization: Bearer <token>`)
- AI agents must have valid user credentials
- Rate limiting recommended for production
- Audit logging for all transactions created via MCP

---

## Files Created

```
backend/src/main/java/com/bookkeeping/core/mcp/
└── McpController.java
```

---

## Next Steps

- [ ] Add rate limiting for MCP endpoints
- [ ] Add audit logging for AI agent actions
- [ ] Implement streaming responses for large result sets
- [ ] Add tool for budget management
- [ ] Add tool for multi-currency support