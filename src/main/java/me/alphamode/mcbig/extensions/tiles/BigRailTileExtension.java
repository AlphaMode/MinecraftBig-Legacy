package me.alphamode.mcbig.extensions.tiles;

import me.alphamode.mcbig.world.phys.BigVec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.tile.RailTile;
import net.minecraft.world.level.tile.Tile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public interface BigRailTileExtension {
    static boolean isRail(Level level, BigInteger x, int y, BigInteger z) {
        int t = level.getTile(x, y, z);
        return t == Tile.RAIL.id || t == Tile.POWERED_RAIL.id || t == Tile.DETECTOR_RAIL.id;
    }

    record BigRailState(Level level, BigInteger x, int y, BigInteger z, boolean isStraight, List<BigVec3i> connections) {
        public BigRailState(Level level, BigInteger x, int y, BigInteger z) {
            int t = level.getTile(x, y, z);
            int d = level.getData(x, y, z);
            boolean isStraight;
            if (((RailTile)Tile.tiles[t]).isStraight()) {
                isStraight = true;
                d &= -9;
            } else {
                isStraight = false;
            }

            this(level, x, y, z, isStraight, new ArrayList<>());
            this.updateConnections(d);
        }

        private void updateConnections(int railShape) {
            this.connections.clear();
            if (railShape == 0) {
                this.connections.add(new BigVec3i(this.x, this.y, this.z.subtract(BigInteger.ONE)));
                this.connections.add(new BigVec3i(this.x, this.y, this.z.add(BigInteger.ONE)));
            } else if (railShape == 1) {
                this.connections.add(new BigVec3i(this.x.subtract(BigInteger.ONE), this.y, this.z));
                this.connections.add(new BigVec3i(this.x.add(BigInteger.ONE), this.y, this.z));
            } else if (railShape == 2) {
                this.connections.add(new BigVec3i(this.x.subtract(BigInteger.ONE), this.y, this.z));
                this.connections.add(new BigVec3i(this.x.add(BigInteger.ONE), this.y + 1, this.z));
            } else if (railShape == 3) {
                this.connections.add(new BigVec3i(this.x.subtract(BigInteger.ONE), this.y + 1, this.z));
                this.connections.add(new BigVec3i(this.x.add(BigInteger.ONE), this.y, this.z));
            } else if (railShape == 4) {
                this.connections.add(new BigVec3i(this.x, this.y + 1, this.z.subtract(BigInteger.ONE)));
                this.connections.add(new BigVec3i(this.x, this.y, this.z.add(BigInteger.ONE)));
            } else if (railShape == 5) {
                this.connections.add(new BigVec3i(this.x, this.y, this.z.subtract(BigInteger.ONE)));
                this.connections.add(new BigVec3i(this.x, this.y + 1, this.z.add(BigInteger.ONE)));
            } else if (railShape == 6) {
                this.connections.add(new BigVec3i(this.x.add(BigInteger.ONE), this.y, this.z));
                this.connections.add(new BigVec3i(this.x, this.y, this.z.add(BigInteger.ONE)));
            } else if (railShape == 7) {
                this.connections.add(new BigVec3i(this.x.subtract(BigInteger.ONE), this.y, this.z));
                this.connections.add(new BigVec3i(this.x, this.y, this.z.add(BigInteger.ONE)));
            } else if (railShape == 8) {
                this.connections.add(new BigVec3i(this.x.subtract(BigInteger.ONE), this.y, this.z));
                this.connections.add(new BigVec3i(this.x, this.y, this.z.subtract(BigInteger.ONE)));
            } else if (railShape == 9) {
                this.connections.add(new BigVec3i(this.x.add(BigInteger.ONE), this.y, this.z));
                this.connections.add(new BigVec3i(this.x, this.y, this.z.subtract(BigInteger.ONE)));
            }
        }

        private void removeSoftConnections() {
            for (int i = 0; i < this.connections.size(); i++) {
                BigRailState state = this.getRail(this.connections.get(i));
                if (state != null && state.connectsTo(this)) {
                    this.connections.set(i, new BigVec3i(state.x, state.y, state.z));
                } else {
                    this.connections.remove(i--);
                }
            }
        }

        private boolean hasRail(BigInteger x, int y, BigInteger z) {
            if (BigRailTileExtension.isRail(this.level, x, y, z)) {
                return true;
            } else {
                return BigRailTileExtension.isRail(this.level, x, y + 1, z) ? true : BigRailTileExtension.isRail(this.level, x, y - 1, z);
            }
        }

        private BigRailState getRail(BigVec3i pos) {
            if (BigRailTileExtension.isRail(this.level, pos.x(), pos.y(), pos.z())) {
                return new BigRailState(this.level, pos.x(), pos.y(), pos.z());
            } else if (BigRailTileExtension.isRail(this.level, pos.x(), pos.y() + 1, pos.z())) {
                return new BigRailState(this.level, pos.x(), pos.y() + 1, pos.z());
            } else {
                return BigRailTileExtension.isRail(this.level, pos.x(), pos.y() - 1, pos.z()) ? new BigRailState(this.level, pos.x(), pos.y() - 1, pos.z()) : null;
            }
        }

        private boolean connectsTo(BigRailState state) {
            for (int i = 0; i < this.connections.size(); i++) {
                BigVec3i pos = this.connections.get(i);
                if (pos.x().equals(state.x) && pos.z().equals(state.z)) {
                    return true;
                }
            }

            return false;
        }

        private boolean hasConnection(BigInteger x, int y, BigInteger z) {
            for (int i = 0; i < this.connections.size(); i++) {
                BigVec3i pos = this.connections.get(i);
                if (pos.x().equals(x) && pos.z().equals(z)) {
                    return true;
                }
            }

            return false;
        }

        public int countPotentialConnections() {
            int pc = 0;
            if (this.hasRail(this.x, this.y, this.z.subtract(BigInteger.ONE))) {
                pc++;
            }

            if (this.hasRail(this.x, this.y, this.z.add(BigInteger.ONE))) {
                pc++;
            }

            if (this.hasRail(this.x.subtract(BigInteger.ONE), this.y, this.z)) {
                pc++;
            }

            if (this.hasRail(this.x.add(BigInteger.ONE), this.y, this.z)) {
                pc++;
            }

            return pc;
        }

        private boolean canConnectTo(BigRailState state) {
            if (this.connectsTo(state)) {
                return true;
            } else if (this.connections.size() == 2) {
                return false;
            } else if (this.connections.size() == 0) {
                return true;
            } else {
                BigVec3i pos = this.connections.get(0);
                return state.y == this.y && pos.y() == this.y ? true : true;
            }
        }

        private void connectTo(BigRailState state) {
            this.connections.add(new BigVec3i(state.x, state.y, state.z));
            boolean n = this.hasConnection(this.x, this.y, this.z.subtract(BigInteger.ONE));
            boolean s = this.hasConnection(this.x, this.y, this.z.add(BigInteger.ONE));
            boolean w = this.hasConnection(this.x.subtract(BigInteger.ONE), this.y, this.z);
            boolean e = this.hasConnection(this.x.add(BigInteger.ONE), this.y, this.z);
            int connection = -1;
            if (n || s) {
                connection = 0;
            }

            if (w || e) {
                connection = 1;
            }

            if (!this.isStraight) {
                if (s && e && !n && !w) {
                    connection = 6;
                }

                if (s && w && !n && !e) {
                    connection = 7;
                }

                if (n && w && !s && !e) {
                    connection = 8;
                }

                if (n && e && !s && !w) {
                    connection = 9;
                }
            }

            if (connection == 0) {
                if (BigRailTileExtension.isRail(this.level, this.x, this.y + 1, this.z.subtract(BigInteger.ONE))) {
                    connection = 4;
                }

                if (BigRailTileExtension.isRail(this.level, this.x, this.y + 1, this.z.add(BigInteger.ONE))) {
                    connection = 5;
                }
            }

            if (connection == 1) {
                if (BigRailTileExtension.isRail(this.level, this.x.add(BigInteger.ONE), this.y + 1, this.z)) {
                    connection = 2;
                }

                if (BigRailTileExtension.isRail(this.level, this.x.subtract(BigInteger.ONE), this.y + 1, this.z)) {
                    connection = 3;
                }
            }

            if (connection < 0) {
                connection = 0;
            }

            int var7 = connection;
            if (this.isStraight) {
                var7 = this.level.getData(this.x, this.y, this.z) & 8 | connection;
            }

            this.level.setData(this.x, this.y, this.z, var7);
        }

        private boolean hasNeighborRail(BigInteger x, int y, BigInteger z) {
            BigRailState state = this.getRail(new BigVec3i(x, y, z));
            if (state == null) {
                return false;
            } else {
                state.removeSoftConnections();
                return state.canConnectTo(this);
            }
        }

        public void place(boolean hasSignal, boolean forceUpdate) {
            boolean n = this.hasNeighborRail(this.x, this.y, this.z.subtract(BigInteger.ONE));
            boolean s = this.hasNeighborRail(this.x, this.y, this.z.add(BigInteger.ONE));
            boolean w = this.hasNeighborRail(this.x.subtract(BigInteger.ONE), this.y, this.z);
            boolean e = this.hasNeighborRail(this.x.add(BigInteger.ONE), this.y, this.z);
            int connection = -1;
            if ((n || s) && !w && !e) {
                connection = 0;
            }

            if ((w || e) && !n && !s) {
                connection = 1;
            }

            if (!this.isStraight) {
                if (s && e && !n && !w) {
                    connection = 6;
                }

                if (s && w && !n && !e) {
                    connection = 7;
                }

                if (n && w && !s && !e) {
                    connection = 8;
                }

                if (n && e && !s && !w) {
                    connection = 9;
                }
            }

            if (connection == -1) {
                if (n || s) {
                    connection = 0;
                }

                if (w || e) {
                    connection = 1;
                }

                if (!this.isStraight) {
                    if (hasSignal) {
                        if (s && e) {
                            connection = 6;
                        }

                        if (w && s) {
                            connection = 7;
                        }

                        if (e && n) {
                            connection = 9;
                        }

                        if (n && w) {
                            connection = 8;
                        }
                    } else {
                        if (n && w) {
                            connection = 8;
                        }

                        if (e && n) {
                            connection = 9;
                        }

                        if (w && s) {
                            connection = 7;
                        }

                        if (s && e) {
                            connection = 6;
                        }
                    }
                }
            }

            if (connection == 0) {
                if (BigRailTileExtension.isRail(this.level, this.x, this.y + 1, this.z.subtract(BigInteger.ONE))) {
                    connection = 4;
                }

                if (BigRailTileExtension.isRail(this.level, this.x, this.y + 1, this.z.add(BigInteger.ONE))) {
                    connection = 5;
                }
            }

            if (connection == 1) {
                if (BigRailTileExtension.isRail(this.level, this.x.add(BigInteger.ONE), this.y + 1, this.z)) {
                    connection = 2;
                }

                if (BigRailTileExtension.isRail(this.level, this.x.subtract(BigInteger.ONE), this.y + 1, this.z)) {
                    connection = 3;
                }
            }

            if (connection < 0) {
                connection = 0;
            }

            this.updateConnections(connection);
            int data = connection;
            if (this.isStraight) {
                data = this.level.getData(this.x, this.y, this.z) & 8 | connection;
            }

            if (forceUpdate || this.level.getData(this.x, this.y, this.z) != data) {
                this.level.setData(this.x, this.y, this.z, data);

                for (int i = 0; i < this.connections.size(); i++) {
                    BigRailState state = this.getRail(this.connections.get(i));
                    if (state != null) {
                        state.removeSoftConnections();
                        if (state.canConnectTo(this)) {
                            state.connectTo(this);
                        }
                    }
                }
            }
        }
    }
}
