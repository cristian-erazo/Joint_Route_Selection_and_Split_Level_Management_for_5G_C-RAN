graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 1
    prc 4
    ant 4
    prb 4
    x 100
    y 35
  ]
  node [
    id 2
    label "2"
    type 1
    prc 3
    ant 10
    prb 5
    x 26
    y 107
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 8
    prb 3
    x 67
    y 85
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 3
    prb 4
    x 48
    y 43
  ]
  edge [
    source 0
    target 1
    bandwith 103
    delay 346
  ]
  edge [
    source 0
    target 4
    bandwith 190
    delay 173
  ]
  edge [
    source 0
    target 3
    bandwith 269
    delay 461
  ]
  edge [
    source 0
    target 2
    bandwith 395
    delay 179
  ]
]
