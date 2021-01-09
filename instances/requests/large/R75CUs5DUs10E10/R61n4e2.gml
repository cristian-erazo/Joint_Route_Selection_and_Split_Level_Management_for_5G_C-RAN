graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 8
    prb 6
    x 33
    y 16
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 6
    prb 5
    x 64
    y 56
  ]
  edge [
    source 0
    target 2
    bandwith 667
    delay 114
  ]
  edge [
    source 1
    target 3
    bandwith 383
    delay 158
  ]
]
